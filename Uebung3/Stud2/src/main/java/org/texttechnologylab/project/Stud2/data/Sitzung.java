package org.texttechnologylab.project.Stud2.data;

import org.bson.Document;

import java.sql.Date;

/**
 * Ein Interface für eine Sitzung
 *
 * @author Stud2
 */
public interface Sitzung extends BundestagObject {
    /**
     * @return die Wahlperiode der Sitzung
     */
    Wahlperiode getWahlperiode();

    /**
     * @return der Ort der Sitzung
     */
    String getOrt();

    /**
     * @return die Nummer der Sitzung
     */
    int getNumber();

    /**
     * @return das Datum der Sitzung
     */
    Date getDate();

    /**
     * @return der Beginn der Sitzung der Form dd.MM.yyyy HH:mm
     */
    Date getBeginn();

    /**
     * @return das Ende der Sitzung der Form dd.MM.yyyy HH:mm
     */
    Date getEnde();

    /**
     * Legt die Tagesordnung der Sitzung fest
     *
     * @param tagesordnung die Tagesordnung
     */
    void setTagesordnung(Tagesordnung tagesordnung);

    /**
     * @return die Tagesordnung der Sitzung
     */
    Tagesordnung getTagesordnung();

    /**
     * @return die Sitzung als Dokument
     */
    Document toDoc();

}
