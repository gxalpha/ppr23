package org.texttechnologylab.project.Parliament_Browser_09_2.data;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Eine Bundestagsrede.
 *
 * @author Stud
 * @author Stud (Änderungen)
 */
public interface Rede {
    /**
     * @return ID der Rede
     */
    String getID();

    /**
     * @return ID des Redners
     */
    String getRednerID();

    /**
     * @return Datum der Rede
     */
    Date getDatum();

    /**
     * @return Text der Rede. Kommentare und Präsidium zählen nicht zum Text
     */
    String getText();

    /**
     * @return Text der Rede als Liste von Strings.
     * @author Stud
     */
    List<String> getTextByElement();


    /**
     * @return Topic der Rede
     */
    String getTopic();

    /**
     * Setzt das Topic der Rede
     *
     * @param topic Topic
     */
    void setTopic(String topic);

    /**
     * @return Gesamtsentiment der Rede
     * @author Stud
     */
    double getSentiment();

    /**
     * Setzt das Gesamtsentiment der Rede
     *
     * @param sentiment Das Sentiment
     * @author Stud
     */
    void setSentiment(double sentiment);

    /**
     * @return Positiver Wert der Rede
     * @author Stud
     */
    double getPositiv();

    /**
     * Setzt das Positiven Wert der Rede
     *
     * @param positiv Positiver Wert
     * @author Stud
     */
    void setPositiv(double positiv);

    /**
     * @return Ngeativer Wert der Rede
     * @author Stud
     */
    double getNegativ();

    /**
     * Setzt den neagtiven Wert der Rede
     *
     * @param negativ Das Sentiment
     * @author Stud
     */
    void setNegativ(double negativ);

    /**
     * @return neutraler Wert der Rede
     * @author Stud
     */
    double getNeutral();

    /**
     * Setzt den neutralen Wert der Rede
     *
     * @param neutral neutraler Wert
     * @author Stud
     */
    void setNeutral(double neutral);

    /**
     * @return Rede-Elemente der Rede
     */
    List<RedeElement> getRedeElemente();

    /**
     * @return CAS-Repräsentation der Rede
     */
    JCas toCAS() throws UIMAException;

    /**
     * @return Objekt in ihrer MongoDB-Repräsentation
     */
    Document toDoc();

    /**
     * Gesamte Rede als LaTeX-Dokument
     *
     * @return String mit LaTeX-Code
     * @author Stud
     */
    String toLaTeX() throws IOException;

}
