package org.texttechnologylab.project.Stud2.data;

import org.texttechnologylab.project.Stud2.data.impl.mongoDB.AbgeordneterMongoDBImpl;
import org.texttechnologylab.project.Stud2.data.impl.mongoDB.RedeMongoDBImpl;
import org.texttechnologylab.project.Stud2.exceptions.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud2.exceptions.BundestagException;
import org.texttechnologylab.project.Stud2.exceptions.RedeNotFoundException;
import org.texttechnologylab.project.Stud2.helper.Tripple;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
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
    Plenarprotokoll parseSitzung(File sitzung)
            throws ParserConfigurationException, IOException, SAXException, ParseException, BundestagException;

    /**
     * @return Gibt eine Liste aller Abgeordneten zurück
     */
    Set<Abgeordneter> listAbgeordnete();

    /**
     * @return Gibt eine Liste aller Fraktionen zurück
     */
    Set<Fraktion> listFraktionen();

    /**
     * @return alle Fraktionen aus der 20. Wahlperiode mit Rednern im Bundestag
     */
    List<String> listFraktionenWithRedenWP20();

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
     * @return alle eingelesenen Sitzungen auf
     */
    List<Plenarprotokoll> listPlenarprotokolle();

    /**
     * Gibt einen Abgeordneten nach ID zurück (aus den eingelesenen Reden)
     *
     * @param ID die ID des zu findenden Abgeordneten
     * @return der Abgeordnete mit der übergebenen ID
     * @throws AbgeordneterNotFoundException falls der Abgeordnete mit der angegebenen ID nicht existiert
     */
    Abgeordneter getAbgeordneterByID(int ID) throws AbgeordneterNotFoundException;

    /**
     *
     * Gibt einen Abgeordneten nach ID zurück (aus den Mongo-DB-Objekten)
     *
     * @param ID die ID des zu findenden Abgeordneten
     * @return der Abgeordnete mit der übergebenen ID
     * @throws AbgeordneterNotFoundException falls der Abgeordnete mit der angegebenen ID nicht existiert
     */
    Abgeordneter getAbgeordneterByIDFromMongoDB(int ID) throws AbgeordneterNotFoundException;

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
    Abgeordneter getAbgeordneterByNameFromMongoDB(String nachname, String vorname) throws BundestagException;

    /**
     * @return die Abgeordneten, die aus der MongoDB eingelesen wurden
     */
    List<AbgeordneterMongoDBImpl> getAbgeordneteDB();

    /**
     * @return die Plenarprotokolle, die aus der MongoDB eingelesen wurden
     */
    Map<String, RedeMongoDBImpl> getRedenDB();

    /**
     * @return die Rede mit der übergebenen ID
     */
    Rede getRedeByIDFromMongoDB(int ID) throws RedeNotFoundException;
}
