package org.texttechnologylab.project.Stud1.data;

import java.util.Date;
import java.util.Set;

/**
 * Ein Abgeordneter.
 * Basiert auf dem Interface aus Aufgabe 2, erweitert dieses aber und fixt Fehler dessen.
 */
public interface Abgeordneter {
    /**
     * @return Nachname des Abgeordneten
     */
    String getID();

    /**
     * @return Nachname des Abgeordneten
     */
    String getNachname();

    /**
     * @return Vorname des Abgeordneten
     */
    String getVorname();

    /**
     * @return Ortszusatz des Abgeordneten
     */
    String getOrtszusatz();

    /**
     * @return Adelszusatz des Abgeordneten
     */
    String getAdelssuffix();

    /**
     * @return Anrede des Abgeordneten
     */
    String getAnrede();

    /**
     * @return Akademischer Titel des Abgeordneten
     */
    String getAkademischerTitel();

    /**
     * @return Geburtsdatum des Abgeordneten
     */
    Date getGeburtsdatum();

    /**
     * @return Geburtsort des Abgeordneten
     */
    String getGeburtsort();

    /**
     * @return Sterbedatum des Abgeordneten
     */
    Date getSterbedatum();

    /**
     * @return Geschlecht des Abgeordneten
     */
    String getGeschlecht();

    /**
     * @return Religionszugehörigkeit des Abgeordneten
     */
    String getReligion();

    /**
     * @return Beruf des Abgeordneten
     */
    String getBeruf();

    /**
     * @return Kurzvita des Abgeordneten
     */
    String getVita();

    /**
     * @return Partei des Abgeordneten
     */
    String getPartei();

    /**
     * @return Fraktion des Abgeordneten
     */
    String getFraktion();

    /**
     * @return Alle Reden des Abgeordneten
     */
    Set<Rede> getReden();
}
