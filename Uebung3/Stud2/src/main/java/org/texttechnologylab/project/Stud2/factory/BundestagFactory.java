package org.texttechnologylab.project.Stud2.factory;

import org.texttechnologylab.project.Stud2.data.Abgeordneter;
import org.texttechnologylab.project.Stud2.data.Fraktion;
import org.texttechnologylab.project.Stud2.data.Kommentar;
import org.texttechnologylab.project.Stud2.data.Mitgliedschaft;
import org.texttechnologylab.project.Stud2.data.Partei;
import org.texttechnologylab.project.Stud2.data.Rede;
import org.texttechnologylab.project.Stud2.data.Sitzung;
import org.texttechnologylab.project.Stud2.data.Tagesordnung;
import org.texttechnologylab.project.Stud2.data.Wahlperiode;
import org.texttechnologylab.project.Stud2.exception.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud2.exception.BundestagException;
import org.texttechnologylab.project.Stud2.utils.Tripple;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

/**
 * Interface für die BundestagFactory
 *
 * @author Stud2
 */
public interface BundestagFactory {

    /**
     * Liest MDB_STAMMDATEN.XML ein
     *
     * @param path Der Pfad zur einzulesenden MDB_STAMMDATEN.XML-Datei
     * @return Ein 3-Tupel der Form (alle Abgeordneten, alle Fraktionen, alle Mitgliedschaften)
     */
    Tripple<Set<Abgeordneter>, Set<Fraktion>, Set<Mitgliedschaft>> parseAbgeordnete(String path)
            throws ParserConfigurationException, IOException, SAXException, ParseException, BundestagException;

    /**
     * Liest alle gehaltenen Reden der Sitzung ein
     *
     * @param sitzung die einzulesende Sitzung als File
     * @return alle gehaltenen Reden in der Sitzung
     */
    Sitzung parseSitzung(File sitzung)
            throws ParserConfigurationException, IOException, SAXException, BundestagException, ParseException;

    /**
     * @param kommentar der String, der im Kommentar-Tag eingespeichert ist
     * @return ein Kommentar-Objekt
     */
    Kommentar parseKommentar(String kommentar, int redeID) throws BundestagException;

    /**
     * @return Gibt eine Liste aller Abgeordneten zurück
     */
    Set<Abgeordneter> listAbgeordnete();

    /**
     * @return Gibt eine Liste aller Fraktionen zurück
     */
    Set<Fraktion> listFraktionen();

    /**
     * @return Gibt eine Liste aller erfassten Parteien zurück
     */
    Set<Partei> listParteien();

    /**
     * @return Gibt eine Liste aller Mitgliedschaften zurück
     */
    Set<Mitgliedschaft> listMitgliedschaften();

    /**
     * @return Gibt eine Liste aller erfassten Wahlperioden zurück
     */
    List<Wahlperiode> getWahlperioden();

    /**
     * @return alle eingelesenen Reden
     */
    List<Rede> listReden();

    /**
     * @return alle eingelesenen Tagesordnungen
     */
    List<Tagesordnung> listTagesordnungen();

    /**
     * @return alle eingelesenen Sitzungen auf
     */
    List<Sitzung> listSitzungen();

    /**
     * @return alle eingelesenen Kommentare
     */
    List<Kommentar> listKommentare();

    /**
     * Gibt einen Abgeordneten nach ID zurück
     *
     * @param ID die ID des zu findenden Abgeordneten
     * @return der Abgeordnete mit der übergebenen ID
     * @throws AbgeordneterNotFoundException falls der Abgeordnete mit der angegebenen ID nicht existiert
     */
    Abgeordneter getAbgeordneterByID(int ID) throws AbgeordneterNotFoundException;

    /**
     * Gibt einen Abgeordneten nach Vor-Nachname und Wahlperiode zurück
     *
     * @param wahlperiode eine beliebige Wahlperiode, in der der Abgeordnete ein Mandat hatte
     * @param nachname    der Nachname des zu zurückzugebenden Abgeordneten
     * @param vorname     der Vorname des zu zurückzugebenden Abgeordneten
     * @return der (hoffentlich) passende Abgeordnete
     */
    Abgeordneter getAbgeordneterByWahlperiodeAndName(int wahlperiode, String nachname, String vorname) throws AbgeordneterNotFoundException;

    /**
     * Gibt einen Abgeordneten nach Vor- und Nachname zurück
     *
     * @param nachname der Nachname des zu zurückzugebenden Abgeordneten
     * @param vorname  der Vorname des zu zurückzugebenden Abgeordneten
     * @return der (hoffentlich) passende Abgeordnete
     */
    Abgeordneter getAbgeordneterByName(String nachname, String vorname) throws BundestagException;
}
