package org.texttechnologylab.project.Parliament_Browser_09_2.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Sitzung;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Tagesordnungspunkt;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Eine Sitzung.
 *
 * @author Stud
 */
public class SitzungImpl implements Sitzung {
    private final String id;
    private final int wahlperiode;
    private final int sitzungsnummer;
    private final Date date;
    private final Date beginn;
    private final Date ende;
    private final List<Tagesordnungspunkt> tagesordnungspunkte;

    /**
     * Konstruktor
     *
     * @param wahlperiode         Wahlperiode der Sitzung
     * @param sitzungsnummer      Sitzungsnummer
     * @param beginn              Beginn der Sitzung
     * @param ende                Ende der Sitzung
     * @param tagesordnungspunkte Tagesordnungspunkte der Sitzung
     */
    public SitzungImpl(int wahlperiode, int sitzungsnummer, Date date, Date beginn, Date ende, List<Tagesordnungspunkt> tagesordnungspunkte) {
        this.wahlperiode = wahlperiode;
        this.sitzungsnummer = sitzungsnummer;
        this.id = "WP" + wahlperiode + "-" + sitzungsnummer;
        this.date = date;
        this.beginn = beginn;
        this.ende = ende;
        this.tagesordnungspunkte = tagesordnungspunkte;
    }

    public SitzungImpl(Document document) {
        this.wahlperiode = document.getInteger("wahlperiode");
        this.sitzungsnummer = document.getInteger("sitzungsnummer");
        this.id = document.getString("_id");
        this.date = document.getDate("date");
        this.beginn = document.getDate("beginn");
        this.ende = document.getDate("ende");
        this.tagesordnungspunkte = document.getList("tagesordnungspunkte", Document.class)
                .stream()
                .map(TagesordnungspunktImpl::new)
                .collect(Collectors.toList());
    }

    /**
     * @return ID der Sitzung
     */
    @Override
    public String getID() {
        return this.id;
    }

    /**
     * @return Wahlperiode der Sitzung
     */
    @Override
    public int getWahlperiode() {
        return this.wahlperiode;
    }

    /**
     * @return Sitzungsnummer
     */
    @Override
    public int getSitzungsnummer() {
        return this.sitzungsnummer;
    }

    /**
     * @return Datum der Sitzung
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return Beginn der Sitzung
     */
    @Override
    public Date getBeginn() {
        return this.beginn;
    }

    /**
     * @return Ende der Sitzung
     */
    @Override
    public Date getEnde() {
        return this.ende;
    }

    /**
     * @return Tagesordnungspunkte der Sitzung
     */
    @Override
    public List<Tagesordnungspunkt> getTagesordnungspunkte() {
        return this.tagesordnungspunkte;
    }

    /**
     * @return Objekt in ihrer MongoDB-Repräsentation
     */
    @Override
    public Document toDoc() {
        Document document = new Document();
        document.put("_id", this.getID());
        document.put("wahlperiode", this.getWahlperiode());
        document.put("sitzungsnummer", this.getSitzungsnummer());
        document.put("date", this.getDate());
        document.put("beginn", this.getBeginn());
        document.put("ende", this.getEnde());

        // Tagesordnungspunkte
        document.put("tagesordnungspunkte", this.getTagesordnungspunkte().stream()
                .map(Tagesordnungspunkt::toDoc)
                .collect(Collectors.toList()));

        return document;
    }

    /**
     * Mache Sitzungsinhalt zu LaTeX-Code
     *
     * @return LaTeX-Code für die Sitzung
     * @author Stud
     */
    @Override
    public String toLaTeX() throws IOException {
        String texCode = "";

        texCode += "\\documentclass{article}\n";
        texCode += "\\usepackage{graphicx}\n\n";

        texCode += "\\title{Protkoll von der Sitzung " + this.sitzungsnummer + ", WP " + this.wahlperiode + "}\n";
        texCode += "\\author{Insight Bundestag 2.0}\n";
        //Todo: Datum einfügen
        texCode += "\n";

        texCode += "\\begin{document}\n\n";

        texCode += "\\maketitle\n\n";

        texCode += "\\tableofcontents\n\n";

        for (Tagesordnungspunkt tagesordnungspunkt : tagesordnungspunkte) {
            texCode += tagesordnungspunkt.toTeX();
            texCode += "\n\n";
        }

        texCode += "\\end{document}\n\n";

        return texCode;
    }
}
