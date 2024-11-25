package org.texttechnologylab.project.Parliament_Browser_09_2.data;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;

/**
 * Ein Rede-Element.
 * Ein Rede-Element ist ein Teil der Rede, d.h. ein Absatz, gewöhnlicher Kommentar oder ein Kommentar des Präsidiums.
 *
 * @author Stud
 */
public interface RedeElement {
    /**
     * @return Typ des Rede-Elements
     */
    RedeElementTyp getTyp();

    /**
     * @return Text des Rede-Elements
     */
    String getText();

    /**
     * @return NLP-Informationen des RedeElements
     * @author Stud
     */
    RedeElementNLP getNLP();

    /**
     * Setzt die NLP-Informationen des RedeElements
     *
     * @param nlp NLP-Informationen
     * @author Stud
     */
    void setNLP(RedeElementNLP nlp);

    /**
     * @return CAS-Repräsentation der Rede
     */
    JCas toCAS() throws UIMAException;

    /**
     * @return Objekt in ihrer MongoDB-Repräsentation
     */
    Document toDoc();

    /**
     * Rede-Element zu LaTeX-Dokument
     *
     * @return String, der LaTeX-code enthält
     */
    String toTeX();

    enum RedeElementTyp {ABSATZ, KOMMENTAR, PRAESIDIUM, ZWISCHENREDNER}

}
