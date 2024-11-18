package org.texttechnologylab.project.Stud1.data.impl;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.texttechnologylab.project.Stud1.data.Abgeordneter;
import org.texttechnologylab.project.Stud1.data.Rede;

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

    /**
     * @param id         ID der Rede
     * @param redner     Redner
     * @param text       Text der Rede
     * @param kommentare Kommentare während Rede
     * @param date       Datum der Rede
     */
    public Rede_File_Impl(String id, Abgeordneter redner, String text, List<String> kommentare, Date date) {
        this.id = id;
        this.redner = redner;
        this.text = text;
        this.kommentare = kommentare;
        this.date = date;
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
     * @return CAS-Repräsentation der Rede
     */
    @Override
    public JCas toCAS() throws UIMAException {
        return JCasFactory.createText(getText(), "de");
    }
}
