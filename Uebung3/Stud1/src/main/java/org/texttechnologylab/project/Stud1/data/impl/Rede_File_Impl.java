package org.texttechnologylab.project.Stud1.data.impl;

import org.texttechnologylab.project.Stud1.data.Abgeordneter;
import org.texttechnologylab.project.Stud1.data.Rede;
import org.texttechnologylab.project.Stud1.data.Sitzung;

import java.util.Date;
import java.util.List;

/**
 * Private Implementierung von Rede
 */
public class Rede_File_Impl implements Rede {
    private final String id;
    private final Abgeordneter redner;
    private final String text;
    private final List<String> kommentare;
    private final Date date;
    private final Sitzung sitzung;

    /**
     * @param id         ID der Rede
     * @param redner     Redner
     * @param text       Text der Rede
     * @param kommentare Kommentare während Rede
     * @param date       Datum der Rede
     */
    public Rede_File_Impl(String id, Abgeordneter redner, String text, List<String> kommentare, Date date, Sitzung sitzung) {
        this.id = id;
        this.redner = redner;
        this.text = text;
        this.kommentare = kommentare;
        this.date = date;
        this.sitzung = sitzung;
    }

    /**
     * @return ID der Rede
     */
    @Override
    public String getID() {
        return id;
    }

    /**
     * @return Redner der Rede
     */
    @Override
    public Abgeordneter getRedner() {
        return redner;
    }

    /**
     * @return Text der Rede
     */
    @Override
    public String getText() {
        return text;
    }

    /**
     * @return Kommentare während der Rede
     */
    @Override
    public List<String> getKommentare() {
        return kommentare;
    }

    /**
     * @return Datum der Rede
     */
    @Override
    public Date getDate() {
        return date;
    }

    /**
     * @return Sitzung der Rede
     */
    @Override
    public Sitzung getSitzung() {
        return sitzung;
    }
}
