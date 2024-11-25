package org.texttechnologylab.project.Parliament_Browser_09_2.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Rede;

import java.time.Instant;
import java.util.*;

/**
 * Ein Abgeordneter.
 * Kombiniert AbgeordneterFileImpl und AbgeordneterMongoDBImpl aus der letzten Abgabe
 *
 * @author Stud
 * @author Stud
 */
public class AbgeordneterImpl implements Abgeordneter {
    private final String id;
    private final List<String> mandate;
    private final List<List<String>> mitgliedschaften;
    private String nachname;
    private String vorname;
    private String ortszusatz;
    private String adelssuffix;
    private String namenspraefix;
    private String anrede;
    private String akademischerTitel;
    private Date geburtsdatum;
    private String geburtsort;
    private Date sterbedatum;
    private String geschlecht;
    private String religion;
    private String beruf;
    private String vita;
    private String partei;
    private String fraktion;
    private Set<Rede> reden = new HashSet<>();
    private Set<String> redeIDs = new HashSet<>();

    /**
     * Konstruktor für einen Abgeordneten beim Einlesen der Stammdaten-Datei ohne Reden
     *
     * @param id                ID des Abgeordneten
     * @param nachname          Nachname des Abgeordneten
     * @param vorname           Vorname des Abgeordneten
     * @param ortszusatz        Ortszusatz des Abgeordneten
     * @param adelssuffix       Adelssuffix des Abgeordneten
     * @param namenspraefix     Namenspraefix des Abgeordneten
     * @param anrede            Anrede des Abgeordneten
     * @param akademischerTitel AkadTitel des Abgeordneten
     * @param geburtsdatum      GebDatum des Abgeordneten
     * @param geburtsort        GebOrt des Abgeordneten
     * @param sterbedatum       SterbeDatum des Abgeordneten
     * @param geschlecht        Geschlecht des Abgeordneten
     * @param religion          Religion des Abgeordneten
     * @param beruf             Beruf des Abgeordneten
     * @param vita              Vita des Abgeordneten
     * @param partei            Partei des Abgeordneten
     * @param fraktion          Fraktion des Abgeordneten
     * @param mandate           Mandate des Abgeordneten
     * @param mitgliedschaften  Mitgliedschaften des Abgeordneten
     * @author Stud
     */
    public AbgeordneterImpl(String id, String nachname, String vorname, String ortszusatz, String adelssuffix, String namenspraefix, String anrede, String akademischerTitel, Date geburtsdatum, String geburtsort, Date sterbedatum, String geschlecht, String religion, String beruf, String vita, String partei, String fraktion, List<String> mandate, List<List<String>> mitgliedschaften) {
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
    }

    /**
     * Konstruktor für einen Abgeordneten, wenn das Document aus der Datenbank vorliegt
     *
     * @param document Dokument aus der MongoDB
     * @author Stud
     */
    public AbgeordneterImpl(Document document) {
        this.id = document.getString(Keys.ID);
        this.nachname = document.getString(Keys.NACHNAME);
        this.vorname = document.getString(Keys.VORNAME);
        this.ortszusatz = document.getString(Keys.ORTSZUSATZ);
        this.adelssuffix = document.getString(Keys.ADELSSUFFIX);
        this.namenspraefix = document.getString(Keys.NAMENSPRAEFIX);
        this.anrede = document.getString(Keys.ANREDE);
        this.akademischerTitel = document.getString(Keys.AKADEMISCHER_TITEL);
        this.geburtsdatum = document.getDate(Keys.GEBURTSDATUM);
        this.geburtsort = document.getString(Keys.GEBURTSORT);
        this.sterbedatum = document.getDate(Keys.STERBEDATUM);
        this.geschlecht = document.getString(Keys.GESCHLECHT);
        this.religion = document.getString(Keys.RELIGION);
        this.beruf = document.getString(Keys.BERUF);
        this.vita = document.getString(Keys.VITA);
        this.partei = document.getString(Keys.PARTEI);
        this.fraktion = document.getString(Keys.FRAKTION);
        this.reden = new HashSet<>();
        this.redeIDs = new HashSet<>(document.getList(Keys.REDEN, String.class));
        this.mandate = document.getList(Keys.MANDATE, String.class);
        this.mitgliedschaften = document.getList(Keys.MITGLIEDSCHAFTEN, (Class<List<String>>) (Object) List.class);
    }

    /**
     * Konstruktor, der einen leeren Abgeordneten erstellt
     *
     * @param id ID des Abgeordneten
     * @author Stud
     */
    public AbgeordneterImpl(String id) {
        this.id = id;
        this.nachname = "";
        this.vorname = "";
        this.ortszusatz = "";
        this.adelssuffix = "";
        this.namenspraefix = "";
        this.anrede = "";
        this.akademischerTitel = "";
        this.geburtsdatum = Date.from(Instant.now());
        this.geburtsort = "";
        this.sterbedatum = null;
        this.geschlecht = "";
        this.religion = "";
        this.beruf = "";
        this.vita = "";
        this.partei = "";
        this.fraktion = "";
        this.reden = new HashSet<>();
        this.redeIDs = new HashSet<>();
        this.mandate = new ArrayList<>();
        this.mitgliedschaften = new ArrayList<>();
    }


    /**
     * @return ID des Abgeordneten
     */
    @Override
    public String getID() {
        return this.id;
    }

    /**
     * @return Nachname des Abgeordneten
     */
    @Override
    public String getNachname() {
        return this.nachname;
    }

    /**
     * Ändert den Nachnamen des Abgeordneten
     *
     * @param nachname Neuer Nachname
     */
    @Override
    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    /**
     * @return Vorname des Abgeordneten
     */
    @Override
    public String getVorname() {
        return this.vorname;
    }

    /**
     * Ändert den Vornamen des Abgeordneten
     *
     * @param vorname Neuer Vorname
     */
    @Override
    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    /**
     * @return Ortszusatz des Abgeordneten
     */
    @Override
    public String getOrtszusatz() {
        return this.ortszusatz;
    }

    /**
     * Ändert den Ortszusatz des Abgeordneten
     *
     * @param ortszusatz Neuer Ortszusatz
     */
    @Override
    public void setOrtszusatz(String ortszusatz) {
        this.ortszusatz = ortszusatz;
    }

    /**
     * @return Namenspraefix des Abgeordneten
     */
    @Override
    public String getNamenspraefix() {
        return this.namenspraefix;
    }

    /**
     * Ändert den Namenspraefix des Abgeordneten
     *
     * @param namenspraefix Neuer Namenspraefix
     */
    @Override
    public void setNamenspraefix(String namenspraefix) {
        this.namenspraefix = namenspraefix;
    }

    /**
     * @return Adelszusatz des Abgeordneten
     */
    @Override
    public String getAdelssuffix() {
        return this.adelssuffix;
    }

    /**
     * Ändert den Adelssuffix des Abgeordneten
     *
     * @param adelssuffix Neuer Adelssuffix
     */
    @Override
    public void setAdelssuffix(String adelssuffix) {
        this.adelssuffix = adelssuffix;
    }

    /**
     * @return Anrede des Abgeordneten
     */
    @Override
    public String getAnrede() {
        return this.anrede;
    }

    /**
     * Ändert die Anrede des Abgeordneten
     *
     * @param anrede Neue Anrede
     */
    @Override
    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }

    /**
     * @return Akademischer Titel des Abgeordneten
     */
    @Override
    public String getAkademischerTitel() {
        return this.akademischerTitel;
    }

    /**
     * Ändert den akademischen Titel des Abgeordneten
     *
     * @param akademischerTitel Neuer akademischer Titel
     */
    @Override
    public void setAkademischerTitel(String akademischerTitel) {
        this.akademischerTitel = akademischerTitel;
    }

    /**
     * @return Geburtsdatum des Abgeordneten
     */
    @Override
    public Date getGeburtsdatum() {
        return this.geburtsdatum;
    }

    /**
     * Ändert das Geburtsdatum des Abgeordneten
     *
     * @param geburtsdatum Neues Geburtsdatum
     */
    @Override
    public void setGeburtsdatum(Date geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    /**
     * @return Geburtsort des Abgeordneten
     */
    @Override
    public String getGeburtsort() {
        return this.geburtsort;
    }

    /**
     * Ändert den Geburtsort des Abgeordneten
     *
     * @param geburtsort Neuer Geburtsort
     */
    @Override
    public void setGeburtsort(String geburtsort) {
        this.geburtsort = geburtsort;
    }

    /**
     * @return Sterbedatum des Abgeordneten
     */
    @Override
    public Date getSterbedatum() {
        return this.sterbedatum;
    }

    /**
     * Ändert das Sterbedatum des Abgeordneten
     *
     * @param sterbedatum Neues Sterbedatum
     */
    @Override
    public void setSterbedatum(Date sterbedatum) {
        this.sterbedatum = sterbedatum;
    }

    /**
     * @return Geschlecht des Abgeordneten
     */
    @Override
    public String getGeschlecht() {
        return this.geschlecht;
    }

    /**
     * Ändert das Geschlecht des Abgeordneten
     *
     * @param geschlecht Neues Geschlecht
     */
    @Override
    public void setGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
    }

    /**
     * @return Religionszugehörigkeit des Abgeordneten
     */
    @Override
    public String getReligion() {
        return this.religion;
    }

    /**
     * Ändert die Religion des Abgeordneten
     *
     * @param religion Neue Religion
     */
    @Override
    public void setReligion(String religion) {
        this.religion = religion;
    }

    /**
     * @return Beruf des Abgeordneten
     */
    @Override
    public String getBeruf() {
        return this.beruf;
    }

    /**
     * Ändert den Beruf des Abgeordneten
     *
     * @param beruf Neuer Beruf
     */
    @Override
    public void setBeruf(String beruf) {
        this.beruf = beruf;
    }

    /**
     * @return Kurzvita des Abgeordneten
     */
    @Override
    public String getVita() {
        return this.vita;
    }

    /**
     * Ändert die Kurzvita des Abgeordneten
     *
     * @param vita Neue Kurzvita
     */
    @Override
    public void setVita(String vita) {
        this.vita = vita;
    }

    /**
     * @return Partei des Abgeordneten
     */
    @Override
    public String getPartei() {
        return this.partei;
    }

    /**
     * Ändert die Partei des Abgeordneten
     *
     * @param partei Neue Partei
     */
    @Override
    public void setPartei(String partei) {
        this.partei = partei;
    }

    /**
     * @return Fraktion des Abgeordneten
     */
    @Override
    public String getFraktion() {
        return this.fraktion;
    }

    /**
     * Ändert die Fraktion des Abgeordneten
     *
     * @param fraktion Neue Fraktion
     */
    @Override
    public void setFraktion(String fraktion) {
        this.fraktion = fraktion;
    }

    /**
     * @return Alle Reden des Abgeordneten.
     * Leer, wenn aus der DB stammend.
     */
    @Override
    public Set<Rede> getReden() {
        return this.reden;
    }

    /**
     * @return Alle Reden des Abgeordneten
     */
    @Override
    public Set<String> getRedeIDs() {
        return this.redeIDs;
    }

    /**
     * @return Beschreibung aller Mandate des Abgeordneten
     */
    @Override
    public List<String> getMandate() {
        return this.mandate;
    }

    /**
     * @return Beschreibung aller Mitgliedschaften des Abgeordneten
     */
    @Override
    public List<List<String>> getMitgliedschaften() {
        return this.mitgliedschaften;
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

        return string;

    }

    /**
     * Fügt dem Abgeordneten eine Rede hinzu
     *
     * @param rede Die Rede
     */
    @Override
    public void addRede(Rede rede) {
        this.reden.add(rede);
        this.redeIDs.add(rede.getID());
    }


    /**
     * Hilfsklasse zum Vermeiden von Typos.
     */
    public static class Keys {
        public static final String ID = "_id";
        public static final String NACHNAME = "nachname";
        public static final String VORNAME = "vorname";
        public static final String ORTSZUSATZ = "ortszusatz";
        public static final String ADELSSUFFIX = "adelssuffix";
        public static final String NAMENSPRAEFIX = "namenspraefix";
        public static final String ANREDE = "anrede";
        public static final String AKADEMISCHER_TITEL = "akademischerTitel";
        public static final String GEBURTSDATUM = "geburtsdatum";
        public static final String GEBURTSORT = "geburtsort";
        public static final String STERBEDATUM = "sterbedatum";
        public static final String GESCHLECHT = "geschlecht";
        public static final String RELIGION = "religion";
        public static final String BERUF = "beruf";
        public static final String VITA = "vita";
        public static final String PARTEI = "partei";
        public static final String FRAKTION = "fraktion";
        public static final String REDEN = "reden_ids";
        public static final String MANDATE = "mandate";
        public static final String MITGLIEDSCHAFTEN = "mitgliedschaften";
    }

}
