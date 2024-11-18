package org.texttechnologylab.project.Stud2.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.data.Abgeordneter;
import org.texttechnologylab.project.Stud2.data.Kommentar;
import org.texttechnologylab.project.Stud2.data.Rede;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Klasse für eine Rede
 *
 * @author Stud2
 */
public class Rede_File_Impl extends BundestagObject_Impl implements Rede {
    private final int ID;
    private final Abgeordneter redner;
    private final String text;
    private final Date date;
    private final List<Kommentar> kommentare;

    /**
     * Konstruktor für ein Objekt der Klasse Rede
     *
     * @param label der Bezeichner (hier: ID) der Rede
     * @param redner der Abgeordnete, der die Rede hält
     * @param text der Inhalt der Rede
     * @param date das Datum, an dem die Rede gehalten wurde
     */
    public Rede_File_Impl(int ID, String label, Abgeordneter redner, String text, Date date, List<Kommentar> kommentare) {
        super(label);
        this.ID = ID;
        this.redner = redner;
        this.text = text;
        this.date = date;
        this.kommentare = kommentare;
    }

    /**
     * @return die ID der Rede
     */
    @Override
    public Object getID() {
        return this.ID;
    }

    /**
     * @return Gibt den Redner zurück
     */
    @Override
    public Abgeordneter getRedner() {
        return this.redner;
    }

    /**
     * @return Gibt denn Redetext zurück
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     * @return die Kommentare während der Rede
     */
    @Override
    public List<Kommentar> listKommentare() {
        return this.kommentare;
    }

    /**
     * @return Gibt das Datum der Rede zurück
     */
    @Override
    public Date getDate() {
        return this.date;
    }

    /**
     * @return die Rede als Dokument
     */
    @Override
    public Document toDoc() {
        Document document = new Document();
        document.append("_id", this.getID().toString());
        document.append("rednerID", this.getRedner().getID().toString());
        document.append("textOneLine", this.getText());
        document.append("text", Arrays.asList(this.getText().split(" ")));
        document.append("anzahlWorte", Arrays.asList(this.getText().split(" ")).size());
        document.append("datum", this.getDate().getTime());

        List<String> kommentare = new ArrayList<>();
        for (Kommentar kommentar : this.listKommentare()) {
            kommentare.add(kommentar.getID().toString());
        }
        document.append("kommentare", kommentare);

        return document;
    }

}
