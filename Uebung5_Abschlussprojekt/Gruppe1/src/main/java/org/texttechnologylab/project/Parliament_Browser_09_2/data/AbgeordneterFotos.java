package org.texttechnologylab.project.Parliament_Browser_09_2.data;

import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;

import java.io.IOException;

/**
 * Die Fotos eines Abgeordneten.
 *
 * @author Stud
 * @author Stud (Änderungen)
 */
public interface AbgeordneterFotos {
    /**
     * @return ID des Fotos
     */
    String getID();


    /**
     * @return Objekt in ihrer MongoDB-Repräsentation
     */
    Document toDoc();


    /**
     * Get the name of the photographer
     *
     * @return
     */
    String getPhotographer();

    /**
     * Get the description of the picture
     *
     * @return
     */
    String getLabel();

    /**
     * Get information about the content of the picture
     *
     * @return
     */
    String getAlt();

    /**
     * Get the source url of the picture
     *
     * @return
     */
    String getURL();

    /**
     * Check if picture is set to primary.
     *
     * @return
     */
    boolean isPrimary();

    void setPrimary(boolean primary);

    /**
     * Get the ID of the Abgeordneten in the picture.
     *
     * @return
     */
    String getAbgeordnetenID();

    /**
     * Sets the ID of the Abgeordneten in the picture.
     *
     * @throws IOException
     */
    void setAbgeordnetenID(String vorname, String nachname, MongoDBHandler mongoDBHandler) throws IOException;


}
