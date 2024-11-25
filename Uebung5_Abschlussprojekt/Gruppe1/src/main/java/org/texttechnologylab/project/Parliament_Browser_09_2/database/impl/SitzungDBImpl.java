package org.texttechnologylab.project.Parliament_Browser_09_2.database.impl;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Sitzung;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Tagesordnungspunkt;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.SitzungDB;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasse, die Sitzungen und Tagesordnungspunkte in Datenbank schreibt
 *
 * @author Stud
 */
public class SitzungDBImpl implements SitzungDB {

    private final MongoDBHandler mongoDBHandler;

    public SitzungDBImpl(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
    }


    /**
     * @param sitzung Sitzung, die in die Datenbank eingefügt werden soll
     * @return Document der Sitzung
     * @author Stud
     */
    @Override
    public Document sitzungToDocument(Sitzung sitzung) {
        return sitzung.toDoc();
    }


    /**
     * @param tagesordnungspunkt Tagesordnungspunkt, der in die Datenbank eingefügt werden soll
     * @return Document des Tagesordnungspunktes
     * @author Stud
     */
    @Override
    public Document tagesordnungspunktToDocument(Tagesordnungspunkt tagesordnungspunkt) {
        return tagesordnungspunkt.toDoc();
    }


    /**
     * Einfügen von Sitzungen und zugehörigen Tagesordnungspunkten in die Datenbank
     *
     * @param sitzungen Sitzungen, die in die Datenbank eingefügt werden sollen
     */
    @Override
    public void insertSitzungDB(List<Sitzung> sitzungen) {
        // TODO: Eigentlich braucht diese Methode nur eine Sitzung zu nehmen und keine Liste
        MongoCollection<Document> collection = mongoDBHandler.getMongoDatabase().getCollection("sitzungen");
        for (Sitzung sitzung : sitzungen) {

            // TODO Stud: Ich hab das mal auskommentiert, sind TOPs nicht schon in der Sitzung enthalten als Liste?
            //  Da brauchen wir eigentlich keine extra-Collection, oder?
            /*
            List<Tagesordnungspunkt> list_tagesordnungspunkte = sitzung.getTagesordnungspunkte();
            insertTagesOrdnungspunktDB(list_tagesordnungspunkte);
            */

            collection.insertOne(sitzungToDocument(sitzung));

        }
    }


    /**
     * Einfügen von Tagesordnungspunkten in die Datenbank
     *
     * @param tagesordnungspunkte Tagesordnungspunkte einer Sitzung die eingefügt werden sollen
     * @author Stud
     */
    @Override
    public void insertTagesOrdnungspunktDB(List<Tagesordnungspunkt> tagesordnungspunkte) {
        MongoCollection<Document> collection = mongoDBHandler.getMongoDatabase().getCollection("tagesordnungspunkte");
        collection.insertMany(tagesordnungspunkte.stream().map(this::tagesordnungspunktToDocument).collect(Collectors.toList()));

    }


    /**
     * Updatet eine Sitzung in der Datenbank
     *
     * @param sitzung
     * @author Stud
     */
    @Override
    public void updateSitzungDB(Sitzung sitzung) {
        mongoDBHandler.getMongoDatabase().getCollection("sitzungen").replaceOne(Filters.eq("_id", sitzung.getID()), sitzungToDocument(sitzung));
    }

}
