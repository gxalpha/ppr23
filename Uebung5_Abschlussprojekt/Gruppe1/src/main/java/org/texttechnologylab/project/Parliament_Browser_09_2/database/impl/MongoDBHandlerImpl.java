package org.texttechnologylab.project.Parliament_Browser_09_2.database.impl;

import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Rede;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Sitzung;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.AbgeordneterImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.RedeImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.SitzungImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.AbgeordneterDB;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.SitzungDB;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.exceptions.NotFoundException;

import javax.print.Doc;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


/**
 * MongoDBConnectionHandler is a class to establish communication between the programm and a MongoDB.
 * It provides methods to create, read, count, update, aggregate, and delete documents in MongoDB.
 *
 * @author Stud based on code from uebung 1-4 by Stud and Stud
 */
public class MongoDBHandlerImpl implements MongoDBHandler {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection = null;


    /**
     * Constructor for class MongoDbConnectionHandler.
     * Creating a connection to a MongoDB database.
     * <p>
     * vgl. "In Medias Res" Datei
     *
     * @param filepath path of config txt file
     * @throws IOException
     * @author Stud
     */
    public MongoDBHandlerImpl(String filepath) throws IOException {


        String sHostName = getHostName(filepath);
        String sDatabase = getsDatabase(filepath);
        String sUsername = getsUsername(filepath);
        String sPassword = getsPassword(filepath);
        int iHostPort = getiHostPort(filepath);


        // defind credentials (Username, database, password)
        MongoCredential credential = MongoCredential.createScramSha1Credential(sUsername, sDatabase, sPassword.toCharArray());
        // defining Hostname and Port
        ServerAddress seed = new ServerAddress(sHostName, iHostPort);
        List<ServerAddress> seeds = new ArrayList(0);
        seeds.add(seed);
        // defining some Options
        MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(2)
                .socketTimeout(10000)
                .maxWaitTime(10000)
                .connectTimeout(1000)
                .sslEnabled(false)
                .build();

        // connect to MongoDB
        mongoClient = new MongoClient(seeds, credential, options);

        this.mongoDatabase = mongoClient.getDatabase(sDatabase);


    }


    /**
     * Method to get host name property out of given file.
     *
     * @param filepath
     * @return value as String
     * @throws IOException
     * @author Stud
     */
    @Override
    public String getHostName(String filepath) throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(filepath));
        String path = p.getProperty("remote_host");
        return path;
    }


    /**
     * Method to get database property out of given file.
     *
     * @param filepath
     * @return value as String
     * @throws IOException
     * @author Stud
     */
    @Override
    public String getsDatabase(String filepath) throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(filepath));
        String path = p.getProperty("remote_database");
        return path;
    }


    /**
     * Method to get username property out of given file.
     *
     * @param filepath
     * @return value as String
     * @throws IOException
     * @author Stud
     */
    @Override
    public String getsUsername(String filepath) throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(filepath));
        String path = p.getProperty("remote_user");
        return path;
    }


    /**
     * Method to get password property out of given file.
     *
     * @param filepath
     * @return value as String
     * @throws IOException
     * @author Stud
     */
    @Override
    public String getsPassword(String filepath) throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(filepath));
        String path = p.getProperty("remote_password");
        return path;
    }


    /**
     * Method to get host port property out of given file.
     *
     * @param filepath
     * @return value as String
     * @throws IOException
     * @author Stud
     */
    @Override
    public int getiHostPort(String filepath) throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(filepath));
        int path = 0;
        try {
            path = Integer.parseInt(p.getProperty("remote_port"));
        } catch (NumberFormatException e) {
            path = 0;
        }
        return path;
    }


    // Erstellen

    /**
     * Uploads Reden, Sitzungen and RedeCas to database.
     *
     * @param sitzungList Sitzungen that should be uploaded
     * @throws IOException
     * @author Stud
     */
    @Override
    public void readInSitzung(List<Sitzung> sitzungList) throws IOException {

        // insert Sitzungen into DB

        SitzungDB sitzungDB = new SitzungDBImpl(this);

        sitzungDB.insertSitzungDB(sitzungList);


    }


    // Useful

    /**
     * @return the database
     * @author Stud
     */
    @Override
    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    /**
     * @param str name of the collection
     * @author Stud
     */
    @Override
    public void setMongoCollection(String str) {
        this.mongoCollection = mongoDatabase.getCollection(str);
    }


    // Zählen

    /**
     * Method to count documents in a given collection.
     *
     * @param collection
     * @return number of documents
     * @author Stud
     */
    @Override
    public long count_docs(String collection) {
        MongoCollection col = this.mongoDatabase.getCollection(collection);
        return col.countDocuments();
    }


    /**
     * Counts documents of specific query.
     *
     * @param query
     * @param collection collection in which documents should be counted
     * @return number of documents found by query
     * @author Stud
     */
    @Override
    public long countDocuments(BasicDBObject query, String collection) {
        return this.mongoDatabase.getCollection(collection).countDocuments(query);
    }


    // Lesen

    /**
     * Method to get a Document out of collection via given Filter and specific Object.
     *
     * @param filter
     * @param object
     * @param collection
     * @return bson Document.
     * @author Stud
     */
    @Override
    public Document getDocByFilter(String filter, Object object, String collection) {

        MongoCollection col = this.mongoDatabase.getCollection(collection);
        Document document;

        try {
            document = (Document) col.find(Filters.eq(filter, object)).first();
        } catch (NullPointerException e) {
            return null;
        }

        return document;

    }


    /**
     * Method to get a List of Documents out of collection via given Filter and specific Object.
     *
     * @param filter
     * @param object     filter should be of specific object.
     * @param collection
     * @return List of bson Documents.
     * @author Stud
     */
    @Override
    public List<Document> getDocumentsByFilter(String filter, Object object, String collection) {

        MongoCollection col = this.mongoDatabase.getCollection(collection);
        List<Document> documents = new ArrayList<>();

        FindIterable<Document> findIterable = col.find(Filters.eq(filter, object));

        for (Document doc : findIterable) {
            documents.add(doc);
        }

        return documents;

    }

    /**
     * @param filter     Spalte, in der gesucht werden soll
     * @param text       welcher text gesucht werden soll
     * @param collection in welcher collection gesucht werden soll
     * @return Liste der documents, die regex entahlten
     * @author Stud
     */
    @Override
    public List<Document> getDocumentsByRegex(String filter, String text, String collection) {
        this.mongoDatabase.getCollection(collection).createIndex(Indexes.text("volltext"));

        // Volltextsuche durch den Text machen nach gegebenem wort
        BasicDBObject searchQuery = BasicDBObject.parse("{ $text: { $search: \"" + text + "\" } }");

        FindIterable<Document> volltextSuche = this.mongoDatabase.getCollection(collection).find(searchQuery);

        List<Document> documentList = new ArrayList<>();

        for (Document volltextDocument : volltextSuche) {
            documentList.add(volltextDocument);
        }

        return documentList;
    }


    /**
     * Selects document with specific id from database collection.
     *
     * @param id         ID of object in database
     * @param collection
     * @return document
     * @author Stud
     */
    @Override
    public Document getDocumentById(String id, String collection) {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", id);
        FindIterable<Document> result = this.mongoDatabase.getCollection(collection).find(query);

        return result.first();

    }


    /**
     * Method to get all the Documents out of a collection.
     *
     * @param collection
     * @return set of Documents.
     * @author Stud
     */
    @Override
    public Set<Document> getAllDocuments(String collection) {

        Set<Document> all = new HashSet<>();

        FindIterable<Document> all_documents = this.mongoDatabase.getCollection(collection).find();
        for (Document doc : all_documents) {
            all.add(doc);
        }

        return all;

    }


    // Updaten

    /**
     * Method to update a Document at specific field.
     *
     * @param fieldname
     * @param object       the value to be updated
     * @param collection
     * @param updatedvalue the new value
     * @author Stud
     */
    @Override
    public void updateDocumentField(String fieldname, Object object, String collection, Bson updatedvalue) {

        MongoCollection col = this.mongoDatabase.getCollection(collection);

        // Search for a Document and update
        Document found = (Document) col.find(new Document(fieldname, object)).first();

        if (found != null) {

            Bson updateoperation = new Document("$set", updatedvalue); //§set so that mongodb nows we are updating an already existing value
            col.updateOne(found, updateoperation);

        }

    }


    /**
     * Method to update a Document.
     *
     * @param document
     * @param collection
     * @author Stud
     */
    @Override
    public void updateDocument(Document document, String collection) {

        MongoCollection col = this.mongoDatabase.getCollection(collection);

        BasicDBObject query = new BasicDBObject();
        query.put("_id", document.getInteger("_id"));
        this.mongoDatabase.getCollection(collection).replaceOne(query, document);

    }

    /**
     * @param document   Document, that should be inserted
     * @param collection In which document should be inserted
     * @author Stud
     */
    @Override
    public void insertDocument(Document document, String collection) {
        this.mongoDatabase.getCollection(collection).insertOne(document);
    }


    // Aggregieren

    /**
     * Aggregates document of a collection according to a query.
     *
     * @param query      the query
     * @param collection the collection
     * @return result of aggregation
     * @author Stud
     */
    @Override
    public AggregateIterable<Document> aggregate(List<Bson> query, String collection) {
        return this.mongoDatabase.getCollection(collection).aggregate(query);
    }


    // Löschen

    /**
     * Deletes document from database.
     *
     * @param id         id of object which should be deleted
     * @param collection collection in which the document appears
     * @author Stud
     */
    @Override
    public void deleteDocument(String id, String collection) {
        this.mongoDatabase.getCollection(collection).deleteOne(getDocumentById(id, collection));
    }

    /**
     * Findet einen Abgeordneten nach seiner ID.
     *
     * @param id ID des Abgeordneten
     * @return Abgeordneter. null wenn nicht gefunden.
     * @author Stud
     */
    @Override
    public Abgeordneter getAbgeordneterByID(String id) {
        Document document = mongoDatabase.getCollection("abgeordnete").find(Filters.eq("_id", id)).first();
        if (document == null) {
            return null;
        }

        return new AbgeordneterImpl(document);
    }

    /**
     * Findet einen Abgerordneten nach seinem Vor- und Nachname.
     *
     * @param vorname  vorname des Abgeordneten
     * @param nachname nachname des Abgeordneten
     * @return Abgeordneter Objekt
     * @author Stud
     */
    public Abgeordneter getAbgeordneterByName(String vorname, String nachname) {
        Document document = mongoDatabase.getCollection("abgeordnete").find(new Document().append("nachname", nachname).append("vorname", vorname)).first();
        if (document == null) {
            return null;
        }

        return new AbgeordneterImpl(document);
    }


    /**
     * Findet Rede nach ihrer ID
     *
     * @param id ID der Rede
     * @return Rede
     * @author Stud
     */
    @Override
    public Rede getRedeByID(String id) {
        Document document = mongoDatabase.getCollection("reden").find(Filters.eq("_id", id)).first();
        if (document == null) {
            return null;
        }

        return new RedeImpl(document);
    }

    /**
     * Findet eine Sitzung nach ihrer ID
     *
     * @param id Id der Sitzung
     * @return Sitzung
     * @author Stud
     */
    @Override
    public Sitzung getSitzungByID(String id) {
        Document document = mongoDatabase.getCollection("sitzungen").find(Filters.eq("_id", id)).first();
        if (document == null) {
            return null;
        }

        return new SitzungImpl(document);
    }

    /**
     * bekomme Rednerbild aus Datenbank
     *
     * @param rednerId Id des redners von dem Bild gebraucht wird
     * @return URL des Bilds
     * @author Stud
     */
    @Override
    public String getImage(String rednerId) {
        Bson query = Filters.and(
                Filters.eq("abgeordnetenID", rednerId),
                Filters.eq("primary", true)
        );

        Document doc = null;

        FindIterable<Document> documents = mongoDatabase.getCollection("bilder").find(query);

        try (MongoCursor<Document> iterator = documents.iterator()) {
            while (iterator.hasNext()) {
                doc = iterator.next();
            }
        } catch (Exception e) {

        }

        if (doc == null) {
            doc = mongoDatabase.getCollection("bilder").find(Filters.eq("abgeordnetenID", rednerId)).first();
        }

        try {
            String url = doc.getString("url");
            return url;
        } catch (NullPointerException e){
            return "";
        }
    }

    /**
     * Fügt eine Rede-ID zum Abgeordneten in der MongoDB hinzu
     *
     * @param rede die Rede, dessen ID zum Abgeordneten in der DB abgespeichert werden soll
     * @author Stud
     */
    public void addRedeIdToAbgeordneter(Rede rede, Abgeordneter abgeordneter) throws NotFoundException {

        // Liste der Reden-IDs des Abgeordneten updaten
        Set<String> reden_ids = abgeordneter.getRedeIDs();

        reden_ids.add(rede.getID());

        AbgeordneterDB db = new AbgeordneterDBImpl(this);
        this.getMongoDatabase().getCollection("abgeordnete").replaceOne(Filters.eq("_id", rede.getRednerID()), db.abgeordneterToDocument(abgeordneter));

    }


    /**
     * Findet eine Liste aller Fotos eines Abgeordneten.
     *
     * @param abgeordnetenID Id des Abgeordneten auf dem Bild.
     * @return Liste aller Fotos
     * @author Stud
     */
    public List<org.bson.Document> getFotosByAbgeordnetenID(String abgeordnetenID) {

        List<Document> fotoList = new ArrayList<>();

        FindIterable<org.bson.Document> documentList = mongoDatabase.getCollection("bilder").find(Filters.eq("abgeordnetenID", abgeordnetenID));

        for (Document doc : documentList) {
            fotoList.add(doc);
        }

        return fotoList;

    }


    /**
     * Findet eine Liste aller Primärbilder eines Abgeordneten.
     *
     * @param abgeordnetenID Id des Abgeordneten auf dem Bild.
     * @return Liste aller Primärbilder
     * @author Stud
     */
    public List<Document> getPrimaryFotosAbgeordnetenID(String abgeordnetenID) {

        List<Document> fotoList = new ArrayList<>();

        FindIterable<org.bson.Document> documentList = mongoDatabase.getCollection("bilder").find(new Document().append("abgeordnetenID", abgeordnetenID).append("primary", true));

        for (Document doc : documentList) {
            fotoList.add(doc);
        }

        return fotoList;

    }


}
