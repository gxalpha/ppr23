package org.texttechnologylab.project.Stud1.data.impl;

import org.texttechnologylab.project.Stud1.data.Abgeordneter;
import org.texttechnologylab.project.Stud1.data.Rede;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Private Implementierung von Abgeordneter
 */
public class Abgeordneter_File_Impl implements Abgeordneter {
    private final String id;
    private final String nachname;
    private final String vorname;
    private final String ortszusatz;
    private final String adelssuffix;
    private final String namenspraefix;
    private final String anrede;
    private final String akademischerTitel;
    private final Date geburtsdatum;
    private final String geburtsort;
    private final Date sterbedatum;
    private final String geschlecht;
    private final String religion;
    private final String beruf;
    private final String vita;
    private final String partei;
    private final String fraktion;
    private final Set<Rede> reden;
    private final List<String> mandate;
    private final List<List<String>> mitgliedschaften;

    /**
     * @param id                ID des Abgeordneten
     * @param vorname           Vorname
     * @param nachname          Nachname
     * @param ortszusatz        Ortszusatz
     * @param adelssuffix       Adelssuffix
     * @param anrede            Anrede des Namen (Dr. etc)
     * @param akademischerTitel Akademischer Titel
     * @param geburtsdatum      Geburtsdatum
     * @param geburtsort        Geburtsort
     * @param sterbedatum       Sterbedatum falls anwendbar, sonst null
     * @param geschlecht        Geschlecht
     * @param religion          Religion
     * @param beruf             Beruf
     * @param vita              Kurzvita falls vorhanden, sonst null
     * @param partei            Partei
     * @param mandate           Mandate des Abgeordneten
     * @param mitgliedschaften  Mandate des Abgeordneten
     */
    public Abgeordneter_File_Impl(String id, String nachname, String vorname, String ortszusatz, String adelssuffix, String namenspraefix, String anrede, String akademischerTitel, Date geburtsdatum, String geburtsort, Date sterbedatum, String geschlecht, String religion, String beruf, String vita, String partei, String fraktion, List<String> mandate, List<List<String>> mitgliedschaften) {
        this.id = id;
        this.nachname = nachname;
        this.vorname = vorname;
        this.ortszusatz = ortszusatz;
        this.adelssuffix = adelssuffix;
        this.namenspraefix = namenspraefix;
        this.anrede = anrede;
        this.akademischerTitel = akademischerTitel;
        this.geburtsdatum = geburtsdatum;
        this.geburtsort = geburtsort;
        this.sterbedatum = sterbedatum;
        this.geschlecht = geschlecht;
        this.religion = religion;
        this.beruf = beruf;
        this.vita = vita;
        this.partei = partei;
        this.fraktion = fraktion;
        this.mandate = mandate;
        this.mitgliedschaften = mitgliedschaften;
        reden = new HashSet<>();
    }

    /**
     * @return Nachname des Abgeordneten
     */
    @Override
    public String getNachname() {
        return nachname;
    }

    /**
     * @return Vorname des Abgeordneten
     */
    @Override
    public String getVorname() {
        return vorname;
    }

    /**
     * @return Ortszusatz der Adresse des Abgeordneten
     */
    @Override
    public String getOrtszusatz() {
        return ortszusatz;
    }

    /**
     * @return Adelssuffix des Abgeordneten
     */
    @Override
    public String getAdelssuffix() {
        return adelssuffix;
    }

    /**
     * @return Namenspraefix des Abgeordneten
     */
    @Override
    public String getNamenspraefix() {
        return namenspraefix;
    }

    /**
     * @return Anrede des Abgeordneten
     */
    @Override
    public String getAnrede() {
        return anrede;
    }

    /**
     * @return Akademischer Titel des Abgeordneten
     */
    @Override
    public String getAkademischerTitel() {
        return akademischerTitel;
    }

    /**
     * @return Geburtsdatum des Abgeordneten
     */
    @Override
    public Date getGeburtsdatum() {
        return geburtsdatum;
    }

    /**
     * @return Geburtsort des Abgeordneten
     */
    @Override
    public String getGeburtsort() {
        return geburtsort;
    }

    /**
     * @return Sterbedatum des Abgeordneten
     */
    @Override
    public Date getSterbedatum() {
        return sterbedatum;
    }

    /**
     * @return Geschlecht des Abgeordneten
     */
    @Override
    public String getGeschlecht() {
        return geschlecht;
    }

    /**
     * @return Religionszugehörigheit des Abgeordneten
     */
    @Override
    public String getReligion() {
        return religion;
    }

    /**
     * @return Beruf des Abgeordneten
     */
    @Override
    public String getBeruf() {
        return beruf;
    }

    /**
     * @return Vita des Abgeordneten
     */
    @Override
    public String getVita() {
        return vita;
    }

    /**
     * @return Partei des Abgeordneten
     */
    @Override
    public String getPartei() {
        return partei;
    }

    /**
     * @return Fraktion des Abgeordneten
     */
    @Override
    public String getFraktion() {
        return fraktion;
    }

    /**
     * @return Alle Reden des Abgeordneten
     */
    @Override
    public Set<Rede> getReden() {
        return new HashSet<>(reden);
    }

    /**
     * @return Alle Reden des Abgeordneten
     */
    @Override
    public Set<String> getRedeIDs() {
        return reden.stream().map(Rede::getID).collect(Collectors.toSet());
    }

    /**
     * @return Beschreibung aller Mandate des Abgeordneten
     */
    @Override
    public List<String> getMandate() {
        return new ArrayList<>(mandate);
    }

    /**
     * @return Beschreibung aller Mitgliedschaften des Abgeordneten
     */
    @Override
    public List<List<String>> getMitgliedschaften() {
        return new ArrayList<>(mitgliedschaften);
    }

    /**
     * Gibt den Namen in formatierter Weise zurück.
     *
     * @return Der formatierte Name
     */
    @Override
    public String getNameFormatted() {
        String string = "";
        if (!getAnrede().isEmpty()) {
            string += getAnrede() + " ";
        }
        string += getVorname() + " ";
        if (!getAdelssuffix().isEmpty()) {
            string += getAdelssuffix() + " ";
        }
        if (!getNamenspraefix().isEmpty()) {
            string += getNamenspraefix() + " ";
        }
        string += getNachname();
        // Add Ortszusatz here as well? Unsure.

        return string;
    }

    /**
     * @return ID des Abgeordneten
     */
    @Override
    public String getID() {
        return id;
    }

    /**
     * Fügt dem Abgeordneten eine Rede hinzu
     *
     * @param rede Die Rede
     */
    void addRede(Rede rede) {
        reden.add(rede);
    }
}