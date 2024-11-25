package org.texttechnologylab.project.Parliament_Browser_09_2.data;

import org.bson.Document;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Eine Sitzung.
 *
 * @author Stud
 */
public interface Sitzung {
    /**
     * @return ID der Sitzung
     */
    String getID();

    /**
     * @return Wahlperiode der Sitzung
     */
    int getWahlperiode();

    /**
     * @return Sitzungsnummer
     */
    int getSitzungsnummer();

    /**
     * @return Beginn der Sitzung
     */
    Date getBeginn();

    /**
     * @return Ende der Sitzung
     */
    Date getEnde();

    /**
     * @return Tagesordnungspunkte der Sitzung
     */
    List<Tagesordnungspunkt> getTagesordnungspunkte();

    /**
     * @return Objekt in ihrer MongoDB-Repräsentation
     */
    Document toDoc();

    /**
     * Mache Sitzungsinhalt zu LaTeX-Code
     *
     * @return LaTeX-Code für die Sitzung
     * @author Stud
     */
    String toLaTeX() throws IOException;
}
