package org.texttechnologylab.project.Parliament_Browser_09_2.core;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.AbgeordneterFotos;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.AbgeordneterImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.MongoDBHandlerImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.downloads.PictureDownloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Klasse die die Bilder der Abgeordneten downloaded und in die Datenbank läd.
 *
 * @author Stud
 */
public class EinlesenFotos {


    public static void main(String[] args) throws IOException {

        MongoDBHandler mongoDBHandler = new MongoDBHandlerImpl("Project_09_02.txt");

        List<Abgeordneter> abgeordneterList = new ArrayList<>();

        // Get all Abgeordnete from DB

        Set<Document> allMdb = mongoDBHandler.getAllDocuments("abgeordnete");
        for (Document mdbDoc : allMdb) {
            Abgeordneter abgeordneter = new AbgeordneterImpl(mdbDoc);
            abgeordneterList.add(abgeordneter);
        }

        // Get name and first name from every Abgeordneter and upload their pictures

        for (Abgeordneter abgeordneter : abgeordneterList) {

            String vorname = abgeordneter.getVorname();
            String nachname = abgeordneter.getNachname();

            uploadPictures(vorname, nachname, mongoDBHandler);

        }

    }


    /**
     * Läd die Bilder in die Datenbank.
     *
     * @param vorname Vorname des Abgeordenten
     * @param nachname Nachname des Abgeordneten
     * @param mongoDBHandler
     * @throws IOException
     * @author Stud
     */
    public static void uploadPictures(String vorname, String nachname, MongoDBHandler mongoDBHandler) throws IOException {

        // Download Pictures
        List<AbgeordneterFotos> abgeordneterFotosList = PictureDownloader.downloadPictures(vorname, nachname);

        // Füge zu jedem Bild die ID des Abgeordneten hinzu
        for (AbgeordneterFotos foto : abgeordneterFotosList) {

            foto.setAbgeordnetenID(vorname, nachname, mongoDBHandler);
            Document document = foto.toDoc();

            MongoCollection<Document> collection = mongoDBHandler.getMongoDatabase().getCollection("bilder");

            // Falls das Bild noch nicht in der Datenbank existiert lade es hoch
            if (!documentExists(collection, document)) {
                collection.insertOne(document);
            } else {
                System.out.println("Document with ID " + document.get("_id") + " already exists in the collection.");
                continue;
            }

        }

    }


    /**
     * Checkt, ob ein Bild bereits in der Datenbank existiert.
     *
     * @param collection Die collection, in der geprüft werden soll.
     * @param document
     * @return boolean
     * @author Stud
     */
    public static boolean documentExists(MongoCollection<Document> collection, Document document) {
        Document filter = new Document("_id", document.get("_id"));
        return collection.countDocuments(filter) > 0;
    }


}
