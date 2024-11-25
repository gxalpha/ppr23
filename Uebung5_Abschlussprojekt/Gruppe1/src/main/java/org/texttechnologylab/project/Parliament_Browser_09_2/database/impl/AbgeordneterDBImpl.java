package org.texttechnologylab.project.Parliament_Browser_09_2.database.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.AbgeordneterImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.AbgeordneterDB;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Klasse, die Abgeordneten in Datenbank schreibt
 *
 * @author Stud
 */
public class AbgeordneterDBImpl implements AbgeordneterDB {

    private final MongoDBHandler mongoDBHandler;

    public AbgeordneterDBImpl(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
    }

    /**
     * @param abgeordneter Abgeordneter, der in Datenbank eingefügt werden soll
     * @return Document des Abgeordneten
     */
    @Override
    public Document abgeordneterToDocument(Abgeordneter abgeordneter) {
        Document document = new Document();
        document.append(AbgeordneterImpl.Keys.ID, abgeordneter.getID());
        document.append(AbgeordneterImpl.Keys.NACHNAME, abgeordneter.getNachname());
        document.append(AbgeordneterImpl.Keys.VORNAME, abgeordneter.getVorname());
        document.append(AbgeordneterImpl.Keys.ORTSZUSATZ, abgeordneter.getOrtszusatz());
        document.append(AbgeordneterImpl.Keys.NAMENSPRAEFIX, abgeordneter.getNamenspraefix());
        document.append(AbgeordneterImpl.Keys.ADELSSUFFIX, abgeordneter.getAdelssuffix());
        document.append(AbgeordneterImpl.Keys.ANREDE, abgeordneter.getAnrede());
        document.append(AbgeordneterImpl.Keys.AKADEMISCHER_TITEL, abgeordneter.getAkademischerTitel());
        document.append(AbgeordneterImpl.Keys.GEBURTSDATUM, abgeordneter.getGeburtsdatum());
        document.append(AbgeordneterImpl.Keys.GEBURTSORT, abgeordneter.getGeburtsort());
        document.append(AbgeordneterImpl.Keys.STERBEDATUM, abgeordneter.getSterbedatum());
        document.append(AbgeordneterImpl.Keys.GESCHLECHT, abgeordneter.getGeschlecht());
        document.append(AbgeordneterImpl.Keys.RELIGION, abgeordneter.getReligion());
        document.append(AbgeordneterImpl.Keys.BERUF, abgeordneter.getBeruf());
        document.append(AbgeordneterImpl.Keys.VITA, abgeordneter.getVita());
        document.append(AbgeordneterImpl.Keys.PARTEI, abgeordneter.getPartei());
        document.append(AbgeordneterImpl.Keys.FRAKTION, abgeordneter.getFraktion());
        document.append(AbgeordneterImpl.Keys.REDEN, abgeordneter.getRedeIDs());
        document.append(AbgeordneterImpl.Keys.MANDATE, abgeordneter.getMandate());
        document.append(AbgeordneterImpl.Keys.MITGLIEDSCHAFTEN, abgeordneter.getMitgliedschaften());

        return document;
    }

    /**
     * Einfügen von Abgeordneten in die Datenbank
     *
     * @param abgeordnete Abgeordnete, die in Datenbank eingefügt werden sollen
     * @author Stud
     * @author Stud (Änderungen)
     */
    @Override
    public void insertAbgeordnetenDB(Set<Abgeordneter> abgeordnete) {
        MongoCollection<Document> collection = mongoDBHandler.getMongoDatabase().getCollection("abgeordnete");
        collection.insertMany(abgeordnete.stream().map(this::abgeordneterToDocument).collect(Collectors.toList()));
    }

    /**
     * Updatet einen Abgeordneten in der Datenbank
     *
     * @param abgeordneter Der Abgeordnete
     * @author Stud
     */
    @Override
    public void updateAbgeordneterDB(Abgeordneter abgeordneter) {
        mongoDBHandler.getMongoDatabase().getCollection("abgeordnete").replaceOne(Filters.eq("_id", abgeordneter.getID()), abgeordneterToDocument(abgeordneter));
    }
}
