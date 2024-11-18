package org.texttechnologylab.project.Stud2.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.data.Sitzung;
import org.texttechnologylab.project.Stud2.data.Tagesordnung;
import org.texttechnologylab.project.Stud2.data.Tagesordnungspunkt;

import java.util.List;

/**
 * Eine Klasse für eine Tagesordnung
 *
 * @author Stud2
 */
public class Tagesordnung_File_Impl extends BundestagObject_Impl implements Tagesordnung {
    private Sitzung sitzung;
    private final List<Tagesordnungspunkt> tagesordnungspunkte;

    /**
     * Konstruktor für eine Tagesordnung
     *
     * @param label der Bezeichner der Tagesordnung
     * @param sitzung die Sitzung der Tagesordnung
     * @param tagesordnungspunkte eine Liste der Tagesordnungspunkte
     */
    public Tagesordnung_File_Impl(String label, Sitzung sitzung, List<Tagesordnungspunkt> tagesordnungspunkte) {
        super(label);
        this.sitzung = sitzung;
        this.tagesordnungspunkte = tagesordnungspunkte;
    }

    /**
     * @return die ID der Tagesordnung (Wahlperiode * 1000 + Sitzungsnummer) * 10
     */
    @Override
    public Object getID() {
        return (int) this.getSitzung().getID() * 10;
    }

    /**
     * @param sitzung die Sitzung der Tagesordnung
     */
    @Override
    public void setSitzung(Sitzung sitzung) {
        this.sitzung = sitzung;
    }

    /**
     * @return die ID der Sitzung der Tagesordnung
     */
    @Override
    public Sitzung getSitzung() {
        return this.sitzung;
    }

    /**
     * @return eine Liste aller Tagesordnungspunkte (diese Objekte enthalten alle Reden etc.)
     */
    @Override
    public List<Tagesordnungspunkt> listTagesordnungspunkte() {
        return this.tagesordnungspunkte;
    }

    /**
     * @return die Tagesordnung als Dokument
     */
    @Override
    public Document toDoc() {
        Document document = new Document();
        document.append("_id", this.getID().toString());
        document.append("sitzung", this.getSitzung().getNumber());

        Document tagesordnungspunkte = new Document();

        for (Tagesordnungspunkt tagesordnungspunkt : this.listTagesordnungspunkte()) {
            tagesordnungspunkte.append(tagesordnungspunkt.getLabel().replace(".", ""), tagesordnungspunkt.toDoc());
        }
        document.append("tagesordnungspunkte", tagesordnungspunkte);

        return document;
    }
}
