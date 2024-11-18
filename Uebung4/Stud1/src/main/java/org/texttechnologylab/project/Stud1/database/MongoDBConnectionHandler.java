package org.texttechnologylab.project.Stud1.database;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.texttechnologylab.project.Stud1.data.Abgeordneter;
import org.texttechnologylab.project.Stud1.data.BundestagFactory;
import org.texttechnologylab.project.Stud1.data.RedeNLP;
import org.texttechnologylab.project.Stud1.data.impl.Abgeordneter_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.RedeNLP_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.Rede_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.website.LogEntry;
import org.texttechnologylab.project.Stud1.website.LogEntry_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.website.SparkHelper;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Connection-Handler for our Datebase
 */
public class MongoDBConnectionHandler {
    private final MongoClient client;
    private final MongoDatabase database;

    /**
     * Erstellt einen MongoDBConnectionHandler
     *
     * @param dbConfig Konfigurationsdatei für die Datenbank
     * @throws IOException Bei Fehlern mit der Konfigurationsdatei
     */
    public MongoDBConnectionHandler(InputStream dbConfig) throws IOException {
        MongoProperties properties = new MongoProperties(dbConfig);

        client = MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(List.of(new ServerAddress(properties.getRemoteHost(), properties.getRemotePort()))))
                .credential(MongoCredential.createScramSha1Credential(properties.getRemoteUser(), properties.getRemoteDatabase(), properties.getRemotePassword().toCharArray()))
                .build()
        );

        database = client.getDatabase(properties.getRemoteDatabase());
    }

    /**
     * Lädt alles der Factory in die Datenbank hoch
     *
     * @param factory Factory
     */
    public void resetBaseData(BundestagFactory factory) {
        database.getCollection(Abgeordneter_MongoDB_Impl.Keys.COLLECTION_NAME).drop();
        database.getCollection(Rede_MongoDB_Impl.Keys.COLLECTION_NAME).drop();

        /* Abgeordnete */
        MongoCollection<Document> abgeordnete = database.getCollection(Abgeordneter_MongoDB_Impl.Keys.COLLECTION_NAME);
        abgeordnete.insertMany(factory.listAbgeordnete().stream().map(MongoDBConversionHelper::toDocument).collect(Collectors.toList()));

        /* Reden */
        MongoCollection<Document> reden = database.getCollection(Rede_MongoDB_Impl.Keys.COLLECTION_NAME);
        reden.insertMany(factory.listReden().stream().map(MongoDBConversionHelper::toDocument).collect(Collectors.toList()));
    }

    /**
     * Lädt ein Reden-JCas in die Datenbank
     *
     * @param redeID Die ID der Rede
     * @param jCas   jCas der analysierten Rede
     */
    public void uploadRedeJCas(String redeID, JCas jCas) throws SAXException {
        MongoCollection<Document> jCasReden = database.getCollection(Rede_MongoDB_Impl.JCasKeys.COLLECTION_NAME);
        jCasReden.insertOne(MongoDBConversionHelper.toDocument(jCas, redeID));
    }

    /**
     * Updatet eine existierende Rede durch das Anfügen von NLP-Informationen.
     *
     * @param redeID   ID der existierenden Rede
     * @param nlpDaten NLP-Daten
     */
    public void updateRedeWithNLPData(String redeID, RedeNLP nlpDaten) {
        Document finder = new Document(Rede_MongoDB_Impl.Keys.ID, redeID);
        Document put = new Document("$set", new Document(Rede_MongoDB_Impl.Keys.NLP, MongoDBConversionHelper.toDocument(nlpDaten)));
        database.getCollection(Rede_MongoDB_Impl.Keys.COLLECTION_NAME).updateOne(finder, put);
    }

    /**
     * Speichert einen neuen Log-Eintrag
     *
     * @param entry Der Eintrag
     */
    public void uploadLogEntry(LogEntry entry) {
        Document document = MongoDBConversionHelper.toDocument(entry);
        database.getCollection(LogEntry_MongoDB_Impl.Keys.COLLECTION_NAME).insertOne(document);
    }

    /**
     * Gibt die Datenbank zurück
     *
     * @return Die Datenbank
     */
    public MongoDatabase getDatabase() {
        return database;
    }

    /**
     * Findet einen Abgeordneten nach seiner ID
     *
     * @param id ID des Abgeordneten
     * @return Abgeordneter. null wenn nicht gefunden.
     */
    public Abgeordneter getAbgeordneterByID(String id) {
        Document abgeordneter = getDatabase()
                .getCollection(Abgeordneter_MongoDB_Impl.Keys.COLLECTION_NAME)
                .find(new Document(Abgeordneter_MongoDB_Impl.Keys.ID, id))
                .first();
        if (abgeordneter != null) {
            return new Abgeordneter_MongoDB_Impl(abgeordneter);
        } else {
            return null;
        }
    }

    /**
     * Findet Abgeordnete nach Namen
     *
     * @param vorname  Vorname
     * @param nachname Nachname
     * @return Liste von zutreffenden Abgeordneten
     */
    public List<Abgeordneter> getAbgeordneteByName(String vorname, String nachname) {
        List<Abgeordneter> abgeordnete = new ArrayList<>();
        getDatabase().getCollection(Abgeordneter_MongoDB_Impl.Keys.COLLECTION_NAME)
                .find(Filters.and(Filters.regex("vorname", vorname, "i"), Filters.regex("nachname", nachname, "i")))
                .map(Abgeordneter_MongoDB_Impl::new)
                .forEach((Consumer<? super Abgeordneter_MongoDB_Impl>) abgeordnete::add);
        return abgeordnete;
    }

    /**
     * Findet eine Rede nach ihrer ID
     *
     * @param id ID der Rede
     * @return Document der Rede. null wenn nicht gefunden.
     */
    public Document getRedeByID(String id) {
        return getDatabase()
                .getCollection(Rede_MongoDB_Impl.Keys.COLLECTION_NAME)
                .find(new Document(Rede_MongoDB_Impl.Keys.ID, id))
                .first();
    }

    /**
     * @param from  Startdatum. Wird ignoriert wenn null.
     * @param until Enddatum. Wird ignoriert wenn null.
     * @return Liste an JSON-Object der Form {"fraktion":"(fraktionskuerzel)", "sentiments": [0.2, 0, -0.3, 0.343, ...]}
     */
    public List<String> getRedeSentimentsJson(Date from, Date until) {
        List<Bson> aggregationPipeline = new ArrayList<>(List.of(
                Aggregates.match(Filters.ne(Rede_MongoDB_Impl.Keys.NLP, null)),
                Aggregates.lookup(Abgeordneter_MongoDB_Impl.Keys.COLLECTION_NAME, Rede_MongoDB_Impl.Keys.REDNER, Abgeordneter_MongoDB_Impl.Keys.ID, "redner_info"),
                Aggregates.unwind("$redner_info"),
                Aggregates.project(
                        Projections.fields(
                                new Document("fraktion", "$redner_info." + Abgeordneter_MongoDB_Impl.Keys.FRAKTION),
                                new Document("sentiment", "$nlp." + RedeNLP_MongoDB_Impl.Keys.SENTIMENT),
                                Projections.exclude(Rede_MongoDB_Impl.Keys.ID)
                        )
                ),
                Aggregates.group("$fraktion", Accumulators.push("sentiments", "$sentiment")),
                Aggregates.project(
                        Projections.fields(
                                new Document("fraktion", "$_id"),
                                Projections.include("sentiments"),
                                Projections.exclude("_id")
                        )
                )
        ));

        /* Insert Date filter to the very beginning, but only if actually required */
        if (from != null) {
            aggregationPipeline.add(0, Aggregates.match(Filters.gte(Rede_MongoDB_Impl.Keys.DATUM, from)));
        }
        if (until != null) {
            aggregationPipeline.add(0, Aggregates.match(Filters.lte(Rede_MongoDB_Impl.Keys.DATUM, until)));
        }

        /* Unfortunately, we have to stringify the values here, as I couldn't get Document::getString to work
         * in Spark.
         * However we can make the document nice and flat in the process (using with a very long Aggregation,
         * see above). */
        List<String> sentimentJsons = new ArrayList<>();
        getDatabase()
                .getCollection(Rede_MongoDB_Impl.Keys.COLLECTION_NAME)
                .aggregate(aggregationPipeline)
                .forEach((Consumer<? super Document>) document -> {
                    String fraktionKurz = SparkHelper.shortenFraktion(document.getString("fraktion"));
                    document.put("fraktion", fraktionKurz);
                    String color = SparkHelper.fraktionenColorMap.getOrDefault(fraktionKurz, "white");
                    document.put("color", color);
                    sentimentJsons.add(document.toJson());
                });
        return sentimentJsons;
    }
}
