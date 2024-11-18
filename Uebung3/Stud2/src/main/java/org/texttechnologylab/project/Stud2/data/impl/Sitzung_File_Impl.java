package org.texttechnologylab.project.Stud2.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.data.Sitzung;
import org.texttechnologylab.project.Stud2.data.Tagesordnung;
import org.texttechnologylab.project.Stud2.data.Wahlperiode;

import java.sql.Date;

/**
 * Eine Klasse für eine Sitzung
 *
 * @author Stud2
 */
public class Sitzung_File_Impl extends BundestagObject_Impl implements Sitzung {

    private final Wahlperiode wahlperiode;
    private final String ort;
    private final int nummer;
    private final Date datum;
    private final Date beginn;
    private final Date ende;
    private Tagesordnung tagesordnung;


    /**
     * Der Konstruktor für eine Sitzung
     *
     * @param label das Label der Sitzung
     * @param wahlperiode die Wahlperiode, in der die Sitzung gehalten wurde
     * @param ort der Ort der Sitzung
     * @param nummer die Sitzungsnummer
     * @param datum das Datum der Sitzung
     * @param beginn das Datum inklusive der Startzeit der Sitzung
     * @param ende das Datum inklusive der Endzeit der Sitzung
     */
    public Sitzung_File_Impl(String label, Wahlperiode wahlperiode, String ort, int nummer,
                             Date datum, Date beginn, Date ende) {
        super(label);
        this.wahlperiode = wahlperiode;
        this.ort = ort;
        this.nummer = nummer;
        this.datum = datum;
        this.beginn = beginn;
        this.ende = ende;
    }

    /**
     * @return die ID der Sitzung (Wahlperiode * 1000 + Sitzungsnummer)
     */
    @Override
    public Object getID() {
        return this.getWahlperiode().getNumber() * 1000 + this.getNumber();
    }

    /**
     * @return die Wahlperiode der Sitzung
     */
    @Override
    public Wahlperiode getWahlperiode() {
        return this.wahlperiode;
    }

    /**
     * @return der Ort der Sitzung
     */
    @Override
    public String getOrt() {
        return this.ort;
    }

    /**
     * @return die Nummer der Sitzung
     */
    @Override
    public int getNumber() {
        return this.nummer;
    }

    /**
     * @return das Datum der Sitzung
     */
    @Override
    public Date getDate() {
        return this.datum;
    }

    /**
     * @return der Beginn der Sitzung der Form dd.MM.yyyy HH:mm
     */
    @Override
    public Date getBeginn() {
        return this.beginn;
    }

    /**
     * @return das Ende der Sitzung der Form dd.MM.yyyy HH:mm
     */
    @Override
    public Date getEnde() {
        return this.ende;
    }

    /**
     * Legt die Tagesordnung der Sitzung fest
     *
     * @param tagesordnung die Tagesordnung
     */
    @Override
    public void setTagesordnung(Tagesordnung tagesordnung) {
        this.tagesordnung = tagesordnung;
    }

    /**
     * @return eine Liste aller Tagesordnungen der Sitzung
     */
    @Override
    public Tagesordnung getTagesordnung() {
        return this.tagesordnung;
    }

    /**
     * @return die Sitzung als Dokument
     */
    @Override
    public Document toDoc() {
        Document document = new Document();
        document.append("_id", this.getID().toString());
        document.append("wahlperiode", this.getWahlperiode().getNumber());
        document.append("sitzungsnummer", this.getNumber());
        document.append("ort", this.getOrt());
        document.append("datum", this.getDate().getTime());
        document.append("beginn", this.getBeginn().getTime());
        document.append("ende", this.getEnde().getTime());
        document.append("tagesordnungID", this.getTagesordnung().getID());

        return document;
    }
}
