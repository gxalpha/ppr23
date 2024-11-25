package org.texttechnologylab.project.Parliament_Browser_09_2.data.impl;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElementCas;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.NlpDB;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.NlpDBImpl;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Implementierung von RedeElementCas
 *
 * @author Stud
 */
public class RedeElementCasImpl implements RedeElementCas {

    private JCas jCas;

    public RedeElementCasImpl(JCas jCas) {
        this.jCas = jCas;
    }

    public RedeElementCasImpl(Document document) throws IOException, UIMAException, SAXException {
        NlpDB nlpDB = new NlpDBImpl();

        this.jCas = nlpDB.deserialiseJCas(document.getString("redeCas"));
    }

    /**
     * @return JCas des RedeElements
     */
    @Override
    public JCas getCas() {
        return this.jCas;
    }

    /**
     * @return Konvertiert das RedeElement zu einem Bson-Document
     */
    @Override
    public Document toDoc() throws SAXException {
        NlpDB nlpDB = new NlpDBImpl();

        Document document = new Document();

        document.append("redeCas", nlpDB.serialiseJCas(this.jCas));

        return document;
    }
}
