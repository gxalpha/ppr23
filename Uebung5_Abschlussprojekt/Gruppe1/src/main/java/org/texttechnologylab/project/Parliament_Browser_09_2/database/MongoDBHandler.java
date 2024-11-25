package org.texttechnologylab.project.Parliament_Browser_09_2.database;


import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Rede;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Sitzung;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.exceptions.NotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Interface for MongoDBHandler.
 *
 * @author Stud
 */
public interface MongoDBHandler {

    /**
     * Method to get host name property out of given file.
     *
     * @param filepath
     * @return value as String
     * @throws IOException
     * @author Stud
     */
    String getHostName(String filepath) throws IOException;

    /**
     * Method to get database property out of given file.
     *
     * @param filepath
     * @return value as String
     * @throws IOException
     * @author Stud
     */
    String getsDatabase(String filepath) throws IOException;

    /**
     * Method to get username property out of given file.
     *
     * @param filepath
     * @return value as String
     * @throws IOException
     * @author Stud
     */
    String getsUsername(String filepath) throws IOException;

    /**
     * Method to get password property out of given file.
     *
     * @param filepath
     * @return value as String
     * @throws IOException
     * @author Stud
     */
    String getsPassword(String filepath) throws IOException;

    /**
     * Method to get host port property out of given file.
     *
     * @param filepath
     * @return value as String
     * @throws IOException
     * @author Stud
     */
    int getiHostPort(String filepath) throws IOException;

    /**
     * Uploads Reden, Sitzungen and RedeCas to database.
     *
     * @param sitzungList Sitzungen that should be uploaded
     * @throws IOException
     * @author Stud
     */
    void readInSitzung(List<Sitzung> sitzungList) throws IOException;

    /**
     * @return the database
     * @author Stud
     */
    MongoDatabase getMongoDatabase();

    /**
     * @param str name of the collection
     * @author Stud
     */
    void setMongoCollection(String str);

    /**
     * Method to count documents in a given collection.
     *
     * @param collection
     * @return number of documents
     * @author Stud
     */
    long count_docs(String collection);

    /**
     * Counts documents of specific query.
     *
     * @param query
     * @param collection collection in which documents should be counted
     * @return number of documents found by query
     * @author Stud
     */
    long countDocuments(BasicDBObject query, String collection);

    /**
     * Method to get a Document out of collection via given Filter and specific Object.
     *
     * @param filter
     * @param object
     * @param collection
     * @return bson Document.
     * @author Stud
     */
    Document getDocByFilter(String filter, Object object, String collection);

    /**
     * Method to get a List of Documents out of collection via given Filter and specific Object.
     *
     * @param filter
     * @param object     filter should be of specific object.
     * @param collection
     * @return List of bson Documents.
     * @author Stud
     */
    List<Document> getDocumentsByFilter(String filter, Object object, String collection);

    /**
     * @param filter     Spalte, in der gesucht werden soll
     * @param text       welcher text gesucht werden soll
     * @param collection in welcher collection gesucht werden soll
     * @return Liste der documents, die regex entahlten
     * @author Stud
     */
    List<Document> getDocumentsByRegex(String filter, String text, String collection);

    /**
     * Selects document with specific id from database collection.
     *
     * @param id         ID of object in database
     * @param collection
     * @return document
     * @author Stud
     */
    Document getDocumentById(String id, String collection);

    /**
     * Method to get all the Documents out of a collection.
     *
     * @param collection
     * @return set of Documents.
     * @author Stud
     */
    Set<Document> getAllDocuments(String collection);

    /**
     * Method to update a Document at specific field.
     *
     * @param fieldname
     * @param object       the value to be updated
     * @param collection
     * @param updatedvalue the new value
     * @author Stud
     */
    void updateDocumentField(String fieldname, Object object, String collection, Bson updatedvalue);

    /**
     * Method to update a Document.
     *
     * @param document
     * @param collection
     * @author Stud
     */
    void updateDocument(Document document, String collection);

    /**
     * @param document   Document, that should be inserted
     * @param collection In which document should be inserted
     * @author Stud
     */
    void insertDocument(Document document, String collection);

    /**
     * Aggregates document of a collection according to a query.
     *
     * @param query      the query
     * @param collection the collection
     * @return result of aggregation
     * @author Stud
     */
    AggregateIterable<Document> aggregate(List<Bson> query, String collection);

    /**
     * Deletes document from database.
     *
     * @param id         id of object which should be deleted
     * @param collection collection in which the document appears
     * @author Stud
     */
    void deleteDocument(String id, String collection);

    /**
     * Findet einen Abgeordneten nach seiner ID.
     *
     * @param id ID des Abgeordneten
     * @return Abgeordneter. null wenn nicht gefunden.
     * @author Stud
     */
    Abgeordneter getAbgeordneterByID(String id);

    /**
     * Findet einen Abgerordneten nach seinem Vor- und Nachname.
     *
     * @param vorname  vorname des Abgeordneten
     * @param nachname nachname des Abgeordneten
     * @return Abgeordneter Objekt
     * @author Stud
     */
    Abgeordneter getAbgeordneterByName(String vorname, String nachname);

    /**
     * Findet eine Rede nach ihrer ID.
     *
     * @param id ID der Rede
     * @return Rede. null, wenn nicht gefunden.
     * @author Stud
     */
    Rede getRedeByID(String id);

    /**
     * Findet eine Sitzung nach ihrer ID
     *
     * @param id Id der Sitzung
     * @return Sitzung
     * @author Stud
     */
    Sitzung getSitzungByID(String id);

    /**
     * bekomme Rednerbild aus Datenbank
     *
     * @param rednerId Id des redners von dem Bild gebraucht wird
     * @return URL des Bilds
     * @author Stud
     */
    String getImage(String rednerId);

    /**
     * Fügt eine Rede-ID zum Abgeordneten in der MongoDB hinzu
     *
     * @param rede die Rede, dessen ID zum Abgeordneten in der DB abgespeichert werden soll
     * @author Stud
     */
    void addRedeIdToAbgeordneter(Rede rede, Abgeordneter abgeordneter) throws NotFoundException;

    /**
     * Findet eine Liste aller Fotos eines Abgeordneten.
     *
     * @param abgeordnetenID Id des Abgeordneten auf dem Bild.
     * @return Liste aller Fotos
     * @author Stud
     */
    List<org.bson.Document> getFotosByAbgeordnetenID(String abgeordnetenID);

    /**
     * Findet eine Liste aller Primärbilder eines Abgeordneten.
     *
     * @param abgeordnetenID Id des Abgeordneten auf dem Bild.
     * @return Liste aller Primärbilder
     * @author Stud
     */
    List<Document> getPrimaryFotosAbgeordnetenID(String abgeordnetenID);

}
