package org.texttechnologylab.project.Stud2.data;

import org.bson.Document;

import java.sql.Date;
import java.util.List;

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
     * @return die Kommentare während der Rede
     */
    List<Kommentar> listKommentare();

    /**
     * @return Gibt das Datum der Rede zurück
     */
    Date getDate();

    /**
     * @return die Rede als Dokument
     */
    Document toDoc();
}
