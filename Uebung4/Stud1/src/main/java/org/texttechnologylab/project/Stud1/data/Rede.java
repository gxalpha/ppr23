package org.texttechnologylab.project.Stud1.data;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;

import java.util.Date;
import java.util.List;

/**
 * Eine Bundestagsrede.
 * Basiert auf dem Interface aus Aufgabe 2, erweitert dieses aber.
 */
public interface Rede {
    /**
     * @return ID der Rede
     */
    String getID();

    /**
     * @return Redner der Rede
     */
    Abgeordneter getRedner();

    /**
     * @return Volltext der Rede
     */
    String getText();

    /**
     * @return Kommentare während der Rede
     */
    List<String> getKommentare();

    /**
     * @return Datum der Rede
     */
    Date getDate();

    /**
     * @return CAS-Repräsentation der Rede
     */
    JCas toCAS() throws UIMAException;
}