package org.texttechnologylab.project.Parliament_Browser_09_2.database.impl;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Rede;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.RedeDB;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.exceptions.NotFoundException;

/**
 * Klasse, die Reden in Datenbank schreibt
 *
 * @author Stud
 * @author Stud (Änderungen)
 */
public class RedeDBImpl implements RedeDB {

    private final MongoDBHandler mongoDBHandler;

    public RedeDBImpl(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
    }

    /**
     * @param rede Rede, die in Datenbank eingefügt werden soll
     * @return Document der Rede
     * @author Stud
     */
    @Override
    public Document redeToDocument(Rede rede) {
        return rede.toDoc();
    }


    /**
     * Einfügen von Reden in die Datenbank
     *
     * @param rede Rede, die in die Datenbank eingefügt werden sollen
     * @author Stud
     */
    @Override
    public void insertRedeDB(Rede rede, Abgeordneter redner) {
        MongoCollection<Document> collection = mongoDBHandler.getMongoDatabase().getCollection("reden");
        ReplaceOptions options = new ReplaceOptions();
        options.upsert(true);
        try {
            this.mongoDBHandler.addRedeIdToAbgeordneter(rede, redner);
        } catch (NotFoundException ignored) {
            return;
        }
        collection.replaceOne(Filters.eq("_id", rede.getID()), rede.toDoc(), options);
    }


    /**
     * Updatet eine Rede in der Datenbank
     *
     * @param rede
     * @author Stud
     */
    @Override
    public void updateRedeDB(Rede rede) {
        mongoDBHandler.getMongoDatabase().getCollection("reden").replaceOne(Filters.eq("_id", rede.getID()), redeToDocument(rede));
    }

}
