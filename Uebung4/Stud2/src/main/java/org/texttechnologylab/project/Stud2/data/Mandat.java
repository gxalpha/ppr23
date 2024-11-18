package org.texttechnologylab.project.Stud2.data;

import org.texttechnologylab.project.Stud2.exceptions.BundestagException;
import org.texttechnologylab.project.Stud2.data.impl.file.Types;

import java.sql.Date;
import java.util.List;
import java.util.Set;

/**
 * Ein Interface für ein Mandat
 *
 * @author Stud2
 */
public interface Mandat extends BundestagObject {
    /**
     * @return Gibt den Abgeordneten des Mandats zurück
     */
    Abgeordneter getAbgeordneter();

    /**
     * @return Gibt den Beginn des Mandates zurück
     */
    Date fromDate();

    /**
     * @return Gibt das Ende des Mandates zurück
     */
    Date toDate();

    /**
     * @return Gibt eine Liste aller Fraktionen während des Mandates zurück
     */
    Set<Fraktion> getFraktionen();

    /**
     * @return Gibt eine Liste aller Ausschüsse während des Mandates zurück
     */
    Set<Ausschuss> listAusschuesse();

    /**
     * @return Gibt alle Mitgliedschaften während des Mandates zurück
     */
    Set<Mitgliedschaft> listMitgliedschaften();

    /**
     * @return alle Gruppenzugehörigkeiten während des Mandats
     */
    List<String> listGruppen();

    /**
     * @return Gibt den Typen des Mandats zurück
     */
    Types.MANDAT getTyp();

    /**
     * @return Gibt die Wahlperiode während des Mandates zurück
     */
    Wahlperiode getWahlperiode();

    /**
     * @return Gibt den Wahlkreis des Mandates zurück
     * @throws BundestagException falls der Wahlkreis null ist
     */
    Wahlkreis getWahlkreis() throws BundestagException;

    /**
     * @return die Liste des Mandats, falls vorhanden (sonst leerer String)
     */
    String getListe();

    /**
     * Fügt eine Fraktion zum Mandat hinzu
     *
     * @param f die hinzuzufügende Fraktion
     */
    void addFraktion(Fraktion f);

    /**
     * Fügt einen Ausschuss zum Mandat hinzu
     *
     * @param a der hinzuzufügende Ausschuss
     */
    void addAusschuss(Ausschuss a);

    /**
     * Fügt eine Mitgliedschaft zum Mandat hinzu
     *
     * @param m die hinzuzufügende Mitgliedschaft
     */
    void addMitgliedschaft(Mitgliedschaft m);
}
