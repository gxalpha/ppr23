package org.texttechnologylab.project.Parliament_Browser_09_2.data;

import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.xml.sax.SAXException;

/**
 * Interface zum Ablegen von RedeElement-Cas-Objekten in der Datenbank
 */
public interface RedeElementCas {
    /**
     * @return JCas des RedeElements
     */
    JCas getCas();

    /**
     * @return Konvertiert das RedeElement zu einem Bson-Document
     */
    Document toDoc() throws SAXException;
}
