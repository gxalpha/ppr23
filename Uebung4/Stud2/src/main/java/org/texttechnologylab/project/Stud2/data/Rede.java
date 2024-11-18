package org.texttechnologylab.project.Stud2.data;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;

import java.sql.Date;

/**
 * Ein Interface für eine Rede
 *
 * @author Stud2
 */
public interface Rede extends BundestagObject {
    /**
     * @return die ID der Rede
     */
    @Override
    Object getID();

    /**
     * @return Gibt den Redner zurück
     */
    Abgeordneter getRedner();

    /**
     * @return Gibt denn Redetext zurück
     */
    String getText();

    /**
     * @return Gibt das Datum der Rede zurück
     */
    Date getDate();

    /**
     * @return die Rede als Dokument
     */
    Document toDoc() throws Exception;
}
