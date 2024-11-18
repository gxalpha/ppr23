package org.texttechnologylab.project.Stud2.data;

import org.bson.Document;

import java.util.List;

/**
 * Ein Interface für eine Tagesordnung einer Sitzung
 *
 * @author Stud2
 */
public interface Tagesordnung extends BundestagObject {
    /**
     * @param sitzung die Sitzung der Tagesordnung
     */
    void setSitzung(Sitzung sitzung);

    /**
     * @return die Sitzung der Tagesordnung
     */
    Sitzung getSitzung();

    /**
     * @return eine Liste aller Tagesordnungspunkte (diese Objekte enthalten alle Reden etc.)
     */
    List<Tagesordnungspunkt> listTagesordnungspunkte();

    /**
     * @return die Tagesordnung als Dokument
     */
    Document toDoc();
}
