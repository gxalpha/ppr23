package org.texttechnologylab.project.Stud2.data;

import org.bson.Document;

import java.sql.Date;
import java.util.List;

/**
 * Ein Interface für eine Sitzung
 *
 * @author Stud2
 */
public interface Plenarprotokoll extends BundestagObject {
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
     * @return alle Reden dieser Sitzung
     */
    List<Rede> getReden();

    /**
     * @return die Sitzung als Dokument
     */
    Document toDoc() throws Exception;

}
