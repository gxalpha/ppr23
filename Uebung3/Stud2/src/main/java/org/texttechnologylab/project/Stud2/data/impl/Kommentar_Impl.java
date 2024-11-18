package org.texttechnologylab.project.Stud2.data.impl;

import org.texttechnologylab.project.Stud2.data.Kommentar;

import org.bson.Document;

/**
 * Eine Klasse für einen Kommentar
 *
 * @author Stud2
 */
public class Kommentar_Impl extends BundestagObject_Impl implements Kommentar {
    private static int counter;
    private final int redeID;
    private final int abgeordnetenID;
    private final String text;

    /**
     * Konstruktor für einen Kommentar
     *
     * @param label der eindeutige Bezeichner des Kommentars
     */
    public Kommentar_Impl(String label, int redeID, int abgeordnetenID, String text) {
        super(label);
        this.redeID = redeID;
        this.abgeordnetenID = abgeordnetenID;
        this.text = text;
    }

    /**
     * @return die ID des Kommentars
     */
    @Override
    public Object getID() {
        return "" + redeID + abgeordnetenID + counter;
    }

    /**
     * @return die ID der Rede, in der kommentiert wurde
     */
    @Override
    public int getRedeID() {
        return this.redeID;
    }

    /**
     * @return der Abgeordnete, der den Kommentar geäußert hat
     */
    @Override
    public int getAbgeordneterID() {
        return this.abgeordnetenID;
    }

    /**
     * @return der Inhalt des Kommentars
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     * @return der Kommentar als Dokument
     */
    @Override
    public Document toDoc() {
        Document doc = new Document();
        doc.append("_id", "" + redeID + abgeordnetenID + counter);
        counter += 1;
        doc.append("redeID", this.getRedeID());
        doc.append("abgeordnetenID", this.getAbgeordneterID());
        doc.append("text", this.getText());
        return doc;
    }
}
