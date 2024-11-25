package org.texttechnologylab.project.Parliament_Browser_09_2.data;

import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.xml.sax.SAXException;

import java.util.List;

/**
 * Interface zum Ablegen von Rede-Cas-Objekten in der Datenbank
 *
 * @author Stud
 */
public interface RedeCas {
    /**
     * @return ID der zugehörigen Rede
     */
    String getRedeID();

    /**
     * @return JCas der Rede
     */
    JCas getCas();

    /**
     * @return Cas-Repräsentation der Elemente der Rede
     */
    List<RedeElementCas> getElemente();

    /**
     * @return Konvertiert die Rede zu einem Bson-Document
     */
    Document toDoc() throws SAXException;
}
