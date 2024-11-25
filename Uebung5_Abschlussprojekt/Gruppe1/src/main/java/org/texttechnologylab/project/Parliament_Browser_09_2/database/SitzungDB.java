package org.texttechnologylab.project.Parliament_Browser_09_2.database;

import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Sitzung;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Tagesordnungspunkt;

import java.util.List;

/**
 * Interface für die Klasse, die Sitzungen und Tagesordnungspunkte in Datenbank schreibt
 *
 * @author Stud
 */
public interface SitzungDB {

    /**
     * @param sitzung Sitzung, die in die Datenbank eingefügt werden soll
     * @return Document der Sitzung
     * @author Stud
     */
    Document sitzungToDocument(Sitzung sitzung);

    /**
     * @param tagesordnungspunkt Tagesordnungspunkt, der in die Datenbank eingefügt werden soll
     * @return Document des Tagesordnungspunktes
     * @author Stud
     */
    Document tagesordnungspunktToDocument(Tagesordnungspunkt tagesordnungspunkt);

    /**
     * Einfügen von Sitzungen und zugehörigen Tagesordnungspunkten in die Datenbank
     *
     * @param sitzungen Sitzungen, die in die Datenbank eingefügt werden sollen
     */
    void insertSitzungDB(List<Sitzung> sitzungen);

    /**
     * Einfügen von Tagesordnungspunkten in die Datenbank
     *
     * @param tagesordnungspunkte Tagesordnungspunkte einer Sitzung die eingefügt werden sollen
     * @author Stud
     */
    void insertTagesOrdnungspunktDB(List<Tagesordnungspunkt> tagesordnungspunkte);

    /**
     * Updatet eine Sitzung in der Datenbank
     *
     * @param sitzung
     * @author Stud
     */
    void updateSitzungDB(Sitzung sitzung);
}
