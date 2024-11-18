package org.texttechnologylab.project.Stud2.database.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.texttechnologylab.project.Stud2.data.Abgeordneter;
import org.texttechnologylab.project.Stud2.data.BundestagObject;
import org.texttechnologylab.project.Stud2.data.Kommentar;
import org.texttechnologylab.project.Stud2.data.Rede;
import org.texttechnologylab.project.Stud2.data.Sitzung;
import org.texttechnologylab.project.Stud2.data.Tagesordnung;
import org.texttechnologylab.project.Stud2.data.impl.Abgeordneter_MongoDB_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Rede_MongoDB_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Sitzung_MongoDB_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Tagesordnung_MongoDB_Impl;
import org.texttechnologylab.project.Stud2.database.MongoDBConnectionHandlerInterface;
import org.texttechnologylab.project.Stud2.exception.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud2.exception.BundestagException;
import org.texttechnologylab.project.Stud2.factory.BundestagFactory;
import org.texttechnologylab.project.Stud2.factory.impl.BundestagFactory_Impl;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ermöglicht die Kommunikation mit einer MongoDB
 *
 * @author Stud2
 */
public class MongoDBConnectionHandler implements MongoDBConnectionHandlerInterface {
    private final BundestagFactory factory;
    private final MongoDatabase database;

    // Die folgenden Listen speichern unsere erzeugten MongoDB_Impl-Objekte:
    private List<Abgeordneter_MongoDB_Impl> abgeordneteDB = new ArrayList<>();
    private List<Sitzung_MongoDB_Impl> sitzungenDB = new ArrayList<>();
    private List<Tagesordnung_MongoDB_Impl> tagesordnungenDB = new ArrayList<>();
    private List<Rede_MongoDB_Impl> redenDB = new ArrayList<>();

    /**
     * Der Konstruktor für einen MongoDBConnectionHandler
     *
     */
    public MongoDBConnectionHandler() throws IOException, BundestagException, ParserConfigurationException, ParseException, SAXException {
        MongoDBProperties_Impl mongoDBProps = new MongoDBProperties_Impl("src/main/resources/MongoDB.properties");

        // Verbindung zur Datenbank aufstellen
        MongoCredential credential = MongoCredential.createCredential(mongoDBProps.getRemoteUser(), mongoDBProps.getRemoteDatabase(),
                mongoDBProps.getRemotePassword().toCharArray());

        MongoClient mongoClient = new MongoClient(
                new ServerAddress(mongoDBProps.getRemoteHost(),
                Integer.parseInt(mongoDBProps.getRemotePort())),
                List.of(credential));

        this.database = mongoClient.getDatabase(mongoDBProps.getRemoteDatabase());

        // Daten einlesen
        this.factory = new BundestagFactory_Impl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "src/main/resources/Bundestagsreden20");

        System.out.println();
        System.out.println("Anzahl eingelesener Reden:                                           " + this.factory.listReden().size());
        System.out.println("Anzahl Kommentare, die einem Abgeordneten zugeordnet werden konnten: " + this.factory.listKommentare().size());


        // Daten in der MongoDB ablegen
        storeDataInMongoDB();

        // Ausgewählte Daten (vgl. Aufgabenstellung) aus der MongoDB auslesen, zu MongoDB_Impl-Objekten überführen und
        // im MongoDBConnectionHandler abspeichern
        getDataFromMongoDB();
    }

    /**
     * Speichert eine Auswahl der eingelesenen Daten in unserer MongoDB ab
     */
    @Override
    public void storeDataInMongoDB() throws BundestagException {

        // Unsere Datenstrukturen in der Datenbank ablegen
        database.drop();
        database.createCollection("Abgeordnete");
        database.createCollection("Sitzungen");
        database.createCollection("Tagesordnungen");
        database.createCollection("Reden");
        database.createCollection("Kommentare");

        System.out.println("\nStoring Abgeordnete in our database ...");
        List<Document> abgeordnete = new ArrayList<>();
        for (Abgeordneter abgeordneter : factory.listAbgeordnete()) {
            abgeordnete.add(abgeordneter.toDoc());
        }
        database.getCollection("Abgeordnete").insertMany(abgeordnete);

        System.out.println("Storing Reden in our database ...");
        List<Document> reden = new ArrayList<>();
        for (Rede rede : factory.listReden()) {
            reden.add(rede.toDoc());
        }
        database.getCollection("Reden").insertMany(reden);

        System.out.println("Storing Sitzungen in our database ...");
        List<Document> sitzungen = new ArrayList<>();
        for (Sitzung sitzung : factory.listSitzungen()) {
            sitzungen.add(sitzung.toDoc());
        }
        database.getCollection("Sitzungen").insertMany(sitzungen);

        System.out.println("Storing Tagesordnungen in our database ...");
        List<Document> tagesordnungen = new ArrayList<>();
        for (Tagesordnung tagesordnung : factory.listTagesordnungen()) {
            tagesordnungen.add(tagesordnung.toDoc());
        }
        database.getCollection("Tagesordnungen").insertMany(tagesordnungen);

        System.out.println("Storing Kommentare in our database ...");
        List<Document> kommentare = new ArrayList<>();
        for (Kommentar kommentar : factory.listKommentare()) {
            kommentare.add(kommentar.toDoc());
        }
        database.getCollection("Kommentare").insertMany(kommentare);

        System.out.println("\n ** Storing data in our MongoDB completed. **\n");
    }

    /**
     * Erzeugt die _MongoDB_Impl-Objekte und speichert sie lokal ab
     */
    @Override
    public void getDataFromMongoDB() throws AbgeordneterNotFoundException {

        System.out.println("Creating <Class>_MongoDB_Impl-Objects ... ");
        // Abgeordneter_MongoDB_Impl-Objekte erzeugen
        FindIterable<Document> abgeordnete = database.getCollection("Abgeordnete").find();
        for (Document abgeordneter : abgeordnete) {
            this.abgeordneteDB.add(new Abgeordneter_MongoDB_Impl(abgeordneter));
        }
        System.out.println(" - #Abgeordnete:      " + this.abgeordneteDB.size());

        // Rede_MongoDB_Impl-Objekte erzeugen
        FindIterable<Document> reden = database.getCollection("Reden").find();
        for (Document rede : reden) {
            this.redenDB.add(new Rede_MongoDB_Impl(rede, this.factory));
        }
        System.out.println(" - #Reden:            " + this.redenDB.size());

        // Tagesordnung_MongoDB_Impl-Objekte erzeugen
        FindIterable<Document> tagesordnungen = database.getCollection("Tagesordnungen").find();
        for (Document t : tagesordnungen) {
            this.tagesordnungenDB.add(new Tagesordnung_MongoDB_Impl(t));
        }
        System.out.println(" - #Tagesordnungen:   " + this.tagesordnungenDB.size());

        // Sitzung_MongoDB_Impl-Objekte erzeugen
        FindIterable<Document> sitzungen = database.getCollection("Sitzungen").find();
        for (Document sitzung : sitzungen) {
            this.sitzungenDB.add(new Sitzung_MongoDB_Impl(sitzung));
        }
        System.out.println(" - #Sitzungen:        " + this.sitzungenDB.size());

        System.out.println("\n ** Initializing <Class>_MongoDB_Impl-Objects completed. **");
    }

    /**
     * ERSTELLT ein Dokument aus einem Bundestag-Objekt und fügt der Collection das Dokument hinzu
     *
     * @param obj        das Bundestagsobjekt, das einzufügen ist
     * @param collection die Collection, in die obj hineinmuss
     */
    @Override
    public void insertDocument(BundestagObject obj, String collection) throws BundestagException {
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
     * @param collection die Collection, in der gesucht werden soll
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
     * @return die erzeugten Abgeordneter_MongoDB_Impl-Objekte
     */
    @Override
    public List<Abgeordneter_MongoDB_Impl> getAbgeordneteDB() {
        return this.abgeordneteDB;
    }

    /**
     * @return die erzeugten Sitzung_MongoDB_Impl-Objekte
     */
    @Override
    public List<Sitzung_MongoDB_Impl> getSitzungenDB() {
        return this.sitzungenDB;
    }

    /**
     * @return die erzeugten Tagesordnung_MongoDB_Impl-Objekte
     */
    @Override
    public List<Tagesordnung_MongoDB_Impl> getTagesordnungenDB() {
        return this.tagesordnungenDB;
    }

    /**
     * @return die erzeugten Rede_MongoDB_Impl-Objekte
     */
    @Override
    public List<Rede_MongoDB_Impl> getRedenDB() {
        return this.redenDB;
    }

    /**
     * @return die MongoDB
     */
    @Override
    public MongoDatabase getDatabase() {
        return this.database;
    }

    /**
     * @return die BundestagFactory
     */
    @Override
    public BundestagFactory getFactory() {
        return this.factory;
    }
}
