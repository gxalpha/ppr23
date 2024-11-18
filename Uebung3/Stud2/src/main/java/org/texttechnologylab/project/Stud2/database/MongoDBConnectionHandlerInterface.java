package org.texttechnologylab.project.Stud2.database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.texttechnologylab.project.Stud2.data.BundestagObject;
import org.texttechnologylab.project.Stud2.data.impl.Abgeordneter_MongoDB_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Rede_MongoDB_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Sitzung_MongoDB_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Tagesordnung_MongoDB_Impl;
import org.texttechnologylab.project.Stud2.exception.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud2.exception.BundestagException;
import org.texttechnologylab.project.Stud2.factory.BundestagFactory;

import java.util.List;

/**
 * Interface für den MongoDBConnectionHandler
 *
 * @author Stud2
 */
public interface MongoDBConnectionHandlerInterface {
    /**
     * Speichert eine Auswahl der eingelesenen Daten in unserer MongoDB ab
     */
    void storeDataInMongoDB() throws BundestagException;

    /**
     * Erzeugt die _MongoDB_Impl-Objekte
     */
    void getDataFromMongoDB() throws AbgeordneterNotFoundException;

    /**
     * ERSTELLT ein Dokument aus einem Bundestag-Objekt und fügt der Collection das Dokument hinzu
     *
     * @param obj        das Bundestagsobjekt, das einzufügen ist
     * @param collection die Collection, in die obj hineinmuss
     */
    void insertDocument(BundestagObject obj, String collection) throws BundestagException;

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
     * @return die erzeugten Abgeordneter_MongoDB_Impl-Objekte
     */
    List<Abgeordneter_MongoDB_Impl> getAbgeordneteDB();

    /**
     * @return die erzeugten Sitzung_MongoDB_Impl-Objekte
     */
    List<Sitzung_MongoDB_Impl> getSitzungenDB();

    /**
     * @return die erzeugten Tagesordnung_MongoDB_Impl-Objekte
     */
    List<Tagesordnung_MongoDB_Impl> getTagesordnungenDB();

    /**
     * @return die erzeugten Rede_MongoDB_Impl-Objekte
     */
    List<Rede_MongoDB_Impl> getRedenDB();

    /**
     * @return die MongoDB
     */
    MongoDatabase getDatabase();

    /**
     * @return die BundestagFactory
     */
    BundestagFactory getFactory();
}
