package org.texttechnologylab.project.Stud2.database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.texttechnologylab.project.Stud2.data.BundestagObject;
import org.texttechnologylab.project.Stud2.data.impl.mongoDB.AbgeordneterMongoDBImpl;
import org.texttechnologylab.project.Stud2.data.impl.mongoDB.RedeMongoDBImpl;
import org.texttechnologylab.project.Stud2.exceptions.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud2.data.BundestagFactory;

import java.util.List;
import java.util.Map;

/**
 * Interface für den MongoDBConnectionHandler
 *
 * @author Stud2
 */
public interface MongoDBConnectionHandler {
    /**
     * Speichert eine Auswahl der eingelesenen Daten in unserer MongoDB ab
     */
    void storeDataInMongoDB(BundestagFactory factory) throws Exception;

    /**
     * ERSTELLT ein Dokument aus einem Bundestag-Objekt und fügt der Collection das Dokument hinzu
     *
     * @param obj        das Bundestagsobjekt, das einzufügen ist
     * @param collection die Collection, in die obj hineinmuss
     */
    void insertDocument(BundestagObject obj, String collection) throws Exception;

    /**
     * LIEST Dokument mit einer bestimmten ID aus einer bestimmten Collection aus
     *
     * @param id         die ID des Objektes in der Datenbank
     * @param collection die Collection, in der gesucht werden soll
     * @return das gesuchte Dokument
     */
    Document getDocument(String id, String collection);

    /**
     * ZÄHLT die Dokumente einer bestimmten Query
     *
     * @param query      die Query
     * @param collection die Collection, in der gezählt werden soll
     * @return die Anzahl der Dokumente, die durch die gegebene Query gefunden werden
     */
    long countDocuments(BasicDBObject query, String collection);

    /**
     * UPDATED ein Dokument in einer Collection anhand dessen ID
     *
     * @param newDoc     das neue Dokument
     * @param collection die Collection, in der gesucht werden soll
     */
    void update(Document newDoc, String collection);

    /**
     * AGGREGIERT Dokumente einer Collection gemäß einer Query
     *
     * @param query      die Query
     * @param collection die Collection
     * @return das Ergebnis der Aggregation
     */
    AggregateIterable<Document> aggregate(List<Bson> query, String collection);

    /**
     * LÖSCHT ein Dokument aus der Datenbank
     *
     * @param id         die ID des zu löschenden Objektes
     * @param collection die Collection, in der sich das Objekt mit der gegebenen ID befindet
     */
    void deleteDocument(String id, String collection);

    /**
     * @return die MongoDB
     */
    MongoDatabase getDatabase();

    /**
     * Erzeugt die AbgeordneteMongoDBImpl-Objekte für die BundestagFactory
     */
    List<AbgeordneterMongoDBImpl> initAbgeordneteMongoDB();

    /**
     * Erzeugt die RedeMongoDBImpl-Objekte für die BundestagFactory
     */
    Map<String, RedeMongoDBImpl> initRedenMongoDB(BundestagFactory factory) throws AbgeordneterNotFoundException;

}
