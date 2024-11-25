package org.texttechnologylab.project.Parliament_Browser_09_2.data;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Ein Abgeordneter.
 *
 * @author Stud
 * @author Stud
 */

public interface Abgeordneter {
    /**
     * @return ID des Abgeordneten
     */
    String getID();

    /**
     * @return Nachname des Abgeordneten
     */
    String getNachname();

    /**
     * Ändert den Nachnamen des Abgeordneten
     *
     * @param nachname Neuer Nachname
     */
    void setNachname(String nachname);

    /**
     * @return Vorname des Abgeordneten
     */
    String getVorname();

    /**
     * Ändert den Vornamen des Abgeordneten
     *
     * @param vorname Neuer Vorname
     */
    void setVorname(String vorname);

    /**
     * @return Ortszusatz des Abgeordneten
     */
    String getOrtszusatz();

    /**
     * Ändert den Ortszusatz des Abgeordneten
     *
     * @param ortszusatz Neuer Ortszusatz
     */
    void setOrtszusatz(String ortszusatz);

    /**
     * @return Namenspraefix des Abgeordneten
     */
    String getNamenspraefix();

    /**
     * Ändert den Namenspraefix des Abgeordneten
     *
     * @param namenspraefix Neuer Namenspraefix
     */
    void setNamenspraefix(String namenspraefix);

    /**
     * @return Adelszusatz des Abgeordneten
     */
    String getAdelssuffix();

    /**
     * Ändert den Adelssuffix des Abgeordneten
     *
     * @param adelssuffix Neuer Adelssuffix
     */
    void setAdelssuffix(String adelssuffix);

    /**
     * @return Anrede des Abgeordneten
     */
    String getAnrede();

    /**
     * Ändert die Anrede des Abgeordneten
     *
     * @param anrede Neue Anrede
     */
    void setAnrede(String anrede);

    /**
     * @return Akademischer Titel des Abgeordneten
     */
    String getAkademischerTitel();

    /**
     * Ändert den akademischen Titel des Abgeordneten
     *
     * @param akademischerTitel Neuer akademischer Titel
     */
    void setAkademischerTitel(String akademischerTitel);

    /**
     * @return Geburtsdatum des Abgeordneten
     */
    Date getGeburtsdatum();

    /**
     * Ändert das Geburtsdatum des Abgeordneten
     *
     * @param geburtsdatum Neues Geburtsdatum
     */
    void setGeburtsdatum(Date geburtsdatum);

    /**
     * @return Geburtsort des Abgeordneten
     */
    String getGeburtsort();

    /**
     * Ändert den Geburtsort des Abgeordneten
     *
     * @param geburtsort Neuer Geburtsort
     */
    void setGeburtsort(String geburtsort);

    /**
     * @return Sterbedatum des Abgeordneten
     */
    Date getSterbedatum();

    /**
     * Ändert das Sterbedatum des Abgeordneten
     *
     * @param sterbedatum Neues Sterbedatum
     */
    void setSterbedatum(Date sterbedatum);

    /**
     * @return Geschlecht des Abgeordneten
     */
    String getGeschlecht();

    /**
     * Ändert das Geschlecht des Abgeordneten
     *
     * @param geschlecht Neues Geschlecht
     */
    void setGeschlecht(String geschlecht);

    /**
     * @return Religionszugehörigkeit des Abgeordneten
     */
    String getReligion();

    /**
     * Ändert die Religion des Abgeordneten
     *
     * @param religion Neue Religion
     */
    void setReligion(String religion);

    /**
     * @return Beruf des Abgeordneten
     */
    String getBeruf();

    /**
     * Ändert den Beruf des Abgeordneten
     *
     * @param beruf Neuer Beruf
     */
    void setBeruf(String beruf);

    /**
     * @return Kurzvita des Abgeordneten
     */
    String getVita();

    /**
     * Ändert die Kurzvita des Abgeordneten
     *
     * @param vita Neue Kurzvita
     */
    void setVita(String vita);

    /**
     * @return Partei des Abgeordneten
     */
    String getPartei();

    /**
     * Ändert die Partei des Abgeordneten
     *
     * @param partei Neue Partei
     */
    void setPartei(String partei);

    /**
     * @return Fraktion des Abgeordneten
     */
    String getFraktion();

    /**
     * Ändert die Fraktion des Abgeordneten
     *
     * @param fraktion Neue Fraktion
     */
    void setFraktion(String fraktion);

    /**
     * @return Alle Reden des Abgeordneten.
     * Leer, wenn aus der DB stammend.
     */
    Set<Rede> getReden();

    /**
     * @return Alle Reden des Abgeordneten
     */
    Set<String> getRedeIDs();

    /**
     * @return Beschreibung aller Mandate des Abgeordneten
     */
    List<String> getMandate();

    /**
     * @return Beschreibung aller Mitgliedschaften des Abgeordneten
     */
    List<List<String>> getMitgliedschaften();

    /**
     * Gibt den Namen in formatierter Weise zurück.
     *
     * @return Der formatierte Name
     */
    String getNameFormatted();

    /**
     * Fügt dem Abgeordneten eine Rede hinzu
     *
     * @param rede Die Rede
     */
    void addRede(Rede rede);

}
