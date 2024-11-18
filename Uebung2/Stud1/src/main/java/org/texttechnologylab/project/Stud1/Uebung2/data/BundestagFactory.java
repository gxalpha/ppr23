package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.Stud1.Uebung2.exceptions.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud1.Uebung2.exceptions.BadDataFormatException;
import org.texttechnologylab.project.Stud1.Uebung2.exceptions.WahlperiodeNotFoundException;
import org.texttechnologylab.project.data.*;

import java.io.File;
import java.util.Set;

/**
 * Extension des vorgegebenen BundestagFactory-Interfaces, um weitere Informationen zurückgeben zu können.
 */
public interface BundestagFactory extends org.texttechnologylab.project.helper.BundestagFactory {
    /**
     * Erstellt eine neue Instanz von BundestagFactory
     *
     * @return Eine neue BundestagFactory-Instanz
     */
    static BundestagFactory newInstance() {
        return new BundestagFactoryImpl();
    }

    /**
     * Listet alle eingelesenen Reden auf
     *
     * @return Liste aller Reden
     */
    Set<Rede> listReden();

    /**
     * Listet alle eingelesenen Parteien auf
     *
     * @return Liste aller Parteien
     */
    Set<Partei> listParteien();

    /**
     * Listet alle eingelesenen Ausschuesse auf
     *
     * @return Liste alle Ausschuesse
     */
    Set<Ausschuss> listAusschuesse();

    /**
     * Listet alle eingelesenen Wahlperioden auf
     *
     * @return Liste aller Wahlperioden
     */
    Set<Wahlperiode> listWahlperioden();

    /**
     * Listet alle eingelesenen Wahlkreise auf
     *
     * @return Liste aller Wahlkreise
     */
    Set<Wahlkreis> listWahlkreise();

    /**
     * Listet alle eingelesenen Landeslisten auf
     *
     * @return Liste aller Landeslisten
     */
    Set<Landesliste> listLandeslisten();

    /**
     * Listet alle eingelesenen Fehltage auf
     *
     * @return Liste aller Fehltage
     */
    Set<Fehltag> listFehltage();

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

    /**
     * Liest eine .xls-Abstimmungs-Datei ein
     *
     * @param abstimmung .xls-Datei mit einer Abstimmung
     * @throws AbgeordneterNotFoundException Wenn ein abstimmender Abgeordneter nicht bekannt ist
     * @throws BadDataFormatException        Wenn die Datei fehlerhaft ist
     * @throws WahlperiodeNotFoundException  Wenn eine Wahlperiode nicht bekannt ist
     */
    void readAbstimmungXls(File abstimmung) throws AbgeordneterNotFoundException, BadDataFormatException, WahlperiodeNotFoundException;

    /**
     * Liest eine .xlsx-Abstimmungs-Datei ein
     *
     * @param abstimmung .xlsx-Datei mit einer Abstimmung
     * @throws AbgeordneterNotFoundException Wenn ein abstimmender Abgeordneter nicht bekannt ist
     * @throws BadDataFormatException        Wenn die Datei fehlerhaft ist
     * @throws WahlperiodeNotFoundException  Wenn eine Wahlperiode nicht bekannt ist
     */
    void readAbstimmungXlsx(File abstimmung) throws AbgeordneterNotFoundException, BadDataFormatException, WahlperiodeNotFoundException;
}
