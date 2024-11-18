package org.texttechnologylab.project.Stud2.data;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.exceptions.BundestagException;
import org.texttechnologylab.project.Stud2.data.impl.file.Types;

import java.sql.Date;
import java.util.List;
import java.util.Set;

/**
 * Ein Interface für einen Abgeordneten
 *
 * @author Stud2
 */
public interface Abgeordneter extends BundestagObject {
    /**
     * @return die ID des Abgeordneten
     */
    Object getID();

    /**
     * @return der Name des Abgeordneten
     */
    String getName();

    /**
     * @return der Vorname des Abgeordneten
     */
    String getVorname();

    /**
     * @return der Ortszusatz des Abgeordneten
     */
    String getOrtszusatz();

    /**
     * @return der Adelszusatz des Abgeordneten
     */
    String getAdelssuffix();

    /**
     * @return die Anrede des Abgeordneten
     */
    String getAnrede();

    /**
     * @return der akademische Titel des Abgeordneten
     */
    String getAkadTitel();

    /**
     * @return das Geburtsdatum des Abgeordneten
     */
    Date getGeburtsDatum();

    /**
     * @return der Geburtsort des Abgeordneten
     */
    String getGeburtsOrt();

    /**
     * @return das Sterbedatum des Abgeordneten
     */
    Date getSterbeDatum();

    /**
     * @return das Geschlecht des Abgeordneten
     */
    Types.GESCHLECHT getGeschlecht();

    /**
     * @return die Religion des Abgeordneten
     */
    String getReligion();

    /**
     * @return der Beruf des Abgeordneten
     */
    String getBeruf();

    /**
     * @return eine kurze Lebensbeschreibung des Abgeordneten
     */
    String getVita();

    /**
     * @return Liste aller Mandate des Abgeordneten
     */
    List<Mandat> listMandate();

    /**
     * @param wahlperiode die Wahlperiode, nach der gefiltert werden soll
     * @return alle Mandate des Abgeordneten nach Wahlperiode
     */
    List<Mandat> listMandate(Wahlperiode wahlperiode);

    /**
     * @param wahlperiode die Wahlperiode, nach der gefiltert werden soll
     * @return Abfrage, ob Abgeordneter in angegebener Wahlperiode ein Mandat hatte
     */
    boolean hasMandat(Wahlperiode wahlperiode);

    /**
     * @return Liste aller Mitgliedschaften des Abgeordneten
     */
    Set<Mitgliedschaft> listMitgliedschaften();

    /**
     * @param wahlperiode die Wahlperiode, nach der gefiltert werden soll
     * @return Liste aller Mitgliedschaften des Abgeordneten in einer Wahlperiode
     */
    Set<Mitgliedschaft> listMitgliedschaften(Wahlperiode wahlperiode);

    /**
     * @return Gibt die Partei des Abgeordneten zurück
     * @throws BundestagException falls der Parteieintrag null ist
     */
    Partei getPartei() throws BundestagException;

    /**
     * @return alle erfassten Reden des Abgeordneten
     */
    public List<Rede> listReden();

    /**
     * Fügt ein Mandat zum Abgeordneten hinzu
     *
     * @param m das hinzuzufügende Mandat
     */
    void addMandat(Mandat m);

    /**
     * Fügt eine Mitgliedschaft zum Abgeordneten hinzu
     *
     * @param m die hinzuzufügende Mitgliedschaft
     */
    void addMitgliedschaft(Mitgliedschaft m);

    /**
     * Fügt eine Rede zum Abgeordneten hinzu
     *
     * @param rede die hinzuzufügende Rede
     */
    void addRede(Rede rede);

    /**
     * @return die IDs der gehaltenen Reden des Abgeordneten
     */
    List<String> getRedenIDs();

    /**
     * @return der Abgeordnete als Dokument
     */
    Document toDoc() throws BundestagException;

}
