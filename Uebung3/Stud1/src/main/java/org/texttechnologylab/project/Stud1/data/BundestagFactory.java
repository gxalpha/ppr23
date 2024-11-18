package org.texttechnologylab.project.Stud1.data;

import org.texttechnologylab.project.Stud1.data.impl.BundestagFactoryImpl;
import org.texttechnologylab.project.Stud1.exceptions.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud1.exceptions.BadDataFormatException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

/**
 * Factory, die Dateien einliest und auf Interfaces die abbildet.
 */
public interface BundestagFactory {
    /**
     * Erstellt eine neue Instanz von BundestagFactory
     *
     * @return Eine neue BundestagFactory-Instanz
     */
    static BundestagFactory newInstance() {
        return new BundestagFactoryImpl();
    }

    /**
     * Listet alle eingelesenen Abgeordneten auf
     *
     * @return Liste aller Abgeordneten
     */
    Set<Abgeordneter> listAbgeordnete();

    /**
     * Listet alle eingelesenen Reden auf
     *
     * @return Liste aller Reden
     */
    Set<Rede> listReden();

    /**
     * Listet alle Sitzungen auf
     *
     * @return Liste aller Sitzungen
     */
    Set<Sitzung> listSitzungen();

    /**
     * Listet alle Tagesordnungen auf
     *
     * @return Liste aller Tagesordnungen
     */
    Set<Tagesordnung> listTagesordnungen();

    /**
     * Liest Stammdaten der Abgeordneten ein
     *
     * @param stammdatenFile MDB_STAMMDATEN.XML file
     * @throws BadDataFormatException When the MDB_STAMMDATEN.DTD is missing, the stammdatenFile file is wrongly formatted, doesn't contain a required element, contains too many elements, etc.
     */
    void readAbgeordnete(File stammdatenFile) throws BadDataFormatException;

    /**
     * Liest Reden einer Redendatei ein.
     *
     * @param reden XML-Datei mit den Reden
     * @throws AbgeordneterNotFoundException Wenn ein Redner nicht in der Menge der Abgeordneten zu finden ist
     * @throws BadDataFormatException        Wenn die dbtplenarprotokoll.dtd-Datei fehlt, die XML-Datei falsch formatiert ist, benötigte Elemente nicht enthält, unbekannte Elemente enthält, etc.
     */
    void readProtokoll(File reden) throws AbgeordneterNotFoundException, BadDataFormatException;
}
