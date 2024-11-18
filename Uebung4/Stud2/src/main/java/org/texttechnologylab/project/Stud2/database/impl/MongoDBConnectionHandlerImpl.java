package org.texttechnologylab.project.Stud2.database.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.texttechnologylab.project.Stud2.data.Abgeordneter;
import org.texttechnologylab.project.Stud2.data.BundestagObject;
import org.texttechnologylab.project.Stud2.data.Rede;
import org.texttechnologylab.project.Stud2.data.impl.mongoDB.AbgeordneterMongoDBImpl;
import org.texttechnologylab.project.Stud2.data.impl.mongoDB.RedeMongoDBImpl;
import org.texttechnologylab.project.Stud2.database.MongoDBConnectionHandler;
import org.texttechnologylab.project.Stud2.exceptions.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud2.data.BundestagFactory;
import org.texttechnologylab.project.Stud2.helper.StringHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse ermöglicht die Kommunikation mit einer MongoDB
 *
 * @author Stud2
 */
public class MongoDBConnectionHandlerImpl implements MongoDBConnectionHandler {
    private final MongoDatabase database;

    /**
     * Der Konstruktor für einen MongoDBConnectionHandler
     *
     */
    public MongoDBConnectionHandlerImpl() throws IOException {
        MongoDBPropertiesImpl mongoDBProps = new MongoDBPropertiesImpl("src/main/resources/MongoDB.properties");

        // Verbindung zur Datenbank aufstellen
        MongoCredential credential = MongoCredential.createCredential(mongoDBProps.getRemoteUser(), mongoDBProps.getRemoteDatabase(),
                mongoDBProps.getRemotePassword().toCharArray());

        MongoClient mongoClient = new MongoClient(
                new ServerAddress(mongoDBProps.getRemoteHost(),
                Integer.parseInt(mongoDBProps.getRemotePort())),
                List.of(credential));

        this.database = mongoClient.getDatabase(mongoDBProps.getRemoteDatabase());
    }

    /**
     * Speichert eine Auswahl der eingelesenen Daten in unserer MongoDB ab
     * ACHTUNG: Löscht alle vorher existierenden Daten!
     */
    public void storeDataInMongoDB(BundestagFactory factory) throws Exception {

        // Unsere Datenstrukturen in der Datenbank ablegen
        database.drop();
        System.out.println(StringHelper.getCurrDateTimeFormatted() + " Database dropped.");
        database.createCollection("Abgeordnete");
        database.createCollection("Reden");

        System.out.println("\n" + StringHelper.getCurrDateTimeFormatted() + " Saving " + factory.listAbgeordnete().size() + " Abgeordnete in MongoDB ...");
        List<Document> abgeordnete = new ArrayList<>();
        for (Abgeordneter abgeordneter : factory.listAbgeordnete()) {
            abgeordnete.add(abgeordneter.toDoc());
        }
        database.getCollection("Abgeordnete").insertMany(abgeordnete);

        System.out.println(StringHelper.getCurrDateTimeFormatted() + " Saving " + factory.listReden().size() + " Reden in MongoDB ...");
        List<Document> reden = new ArrayList<>();
        for (Rede r : factory.listReden()) {
            reden.add(r.toDoc());
        }
        database.getCollection("Reden").insertMany(reden);
    }

    /**
     * Erzeugt die AbgeordneteMongoDBImpl-Objekte für die BundestagFactory
     */
    public List<AbgeordneterMongoDBImpl> initAbgeordneteMongoDB() {

        List<AbgeordneterMongoDBImpl> abgeordneteMongoDB = new ArrayList<>();

        System.out.println(StringHelper.getCurrDateTimeFormatted() + " Initializing AbgeordneterMongoDBImpl-Objects ...");

        // Datenbankabfrage
        FindIterable<Document> abgeordnete = database.getCollection("Abgeordnete").find();
        for (Document abgeordneter : abgeordnete) {
            abgeordneteMongoDB.add(new AbgeordneterMongoDBImpl(abgeordneter));
        }

        return abgeordneteMongoDB;
    }

    /**
     * Erzeugt die RedeMongoDBImpl-Objekte für die BundestagFactory
     */
    public Map<String, RedeMongoDBImpl> initRedenMongoDB(BundestagFactory factory) throws AbgeordneterNotFoundException {

        Map<String, RedeMongoDBImpl> redenMongoDB = new HashMap<>();

        System.out.println(StringHelper.getCurrDateTimeFormatted() + " Initializing RedeMongoDBImpl-Objects ...");

        // Datenbankabfrage (CAS - sofern vorhanden - soll aus Speichergründen initial nicht heruntergeladen werden)
        List<Bson> query = List.of(
                Aggregates.project(Projections.fields(
                        Projections.include("_id"),
                        Projections.include("rednerID"),
                        Projections.include("text"),
                        Projections.include("datum"))));

        AggregateIterable<Document> reden = this.aggregate(query, "Reden");

        for (Document rede : reden) {
            redenMongoDB.put(rede.getString("_id"), new RedeMongoDBImpl(rede, factory));
        }
        return redenMongoDB;
    }

    /**
     * ERSTELLT ein Dokument aus einem Bundestag-Objekt und fügt der Collection das Dokument hinzu
     *
     * @param obj        das Bundestagsobjekt, das einzufügen ist
     * @param collection die Collection, in die obj hineinmuss
     */
    @Override
    public void insertDocument(BundestagObject obj, String collection) throws Exception {
        this.database.getCollection(collection).insertOne(obj.toDoc());
    }

    /**
     * LIEST Dokument mit einer bestimmten ID aus einer bestimmten Collection aus
     *
     * @param id         die ID des Objektes in der Datenbank
     * @param collection die Collection, in der gesucht werden soll
     *
     * @return das gesuchte Dokument
     */
    @Override
    public Document getDocument(String id, String collection){

        BasicDBObject query = new BasicDBObject();
        query.put("_id", id);
        FindIterable<Document> result = this.database.getCollection(collection).find(query);

        return result.first();
    }

    /**
     * ZÄHLT die Dokumente einer bestimmten Query
     *
     * @param query      die Query
     * @param collection die Collection, in der gezählt werden soll
     * @return die Anzahl der Dokumente, die durch die gegebene Query gefunden werden
     */
    @Override
    public long countDocuments(BasicDBObject query, String collection){
        return this.database.getCollection(collection).countDocuments(query);
    }

    /**
     * UPDATED ein Dokument in einer Collection anhand dessen ID
     *
     * @param newDoc das neue Dokument
     * @param collection die Collection, in der ge-updated werden soll
     */
    @Override
    public void update(Document newDoc, String collection) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", newDoc.getString("_id"));
        this.database.getCollection(collection).replaceOne(query, newDoc);
    }

    /**
     * AGGREGIERT Dokumente einer Collection gemäß einer Query
     *
     * @param query      die Query
     * @param collection die Collection
     * @return das Ergebnis der Aggregation
     */
    @Override
    public AggregateIterable<Document> aggregate(List<Bson> query, String collection) {
        return this.database.getCollection(collection).aggregate(query);
    }

    /**
     * LÖSCHT ein Dokument aus der Datenbank
     *
     * @param id         die ID des zu löschenden Objektes
     * @param collection die Collection, in der sich das Objekt mit der gegebenen ID befindet
     */
    @Override
    public void deleteDocument(String id, String collection) {
        this.database.getCollection(collection).deleteOne(getDocument(id, collection));
    }

    /**
     * @return die MongoDB
     */
    @Override
    public MongoDatabase getDatabase() {
        return this.database;
    }
}
