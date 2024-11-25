package org.texttechnologylab.project.Parliament_Browser_09_2.data.impl;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeCas;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElementCas;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.NlpDB;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.NlpDBImpl;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementierung für RedeCas
 *
 * @author Stud
 */
public class RedeCasImpl implements RedeCas {

    private String redeId;
    private JCas jCas;
    private List<RedeElementCas> redeElementCasList;

    /**
     * Konstruktor fürs Parsen
     *
     * @param id             id der Rede
     * @param jCas           JCas der Rede
     * @param redeElementCas JCas der Rede-Elemente
     */
    public RedeCasImpl(String id, JCas jCas, List<RedeElementCas> redeElementCas) {
        this.redeId = id;
        this.jCas = jCas;
        this.redeElementCasList = redeElementCas;
    }

    public RedeCasImpl(Document document) throws IOException, UIMAException, SAXException {
        NlpDB nlpDB = new NlpDBImpl();

        this.redeId = document.getString("_id");
        this.jCas = nlpDB.deserialiseJCas(document.getString("redeCas"));
        //Todo: aus der Datenbank rauslesen redeElementCas
    }

    /**
     * @return ID der zugehörigen Rede
     */
    @Override
    public String getRedeID() {
        return this.redeId;
    }

    /**
     * @return JCas der Rede
     */
    @Override
    public JCas getCas() {
        return this.jCas;
    }

    /**
     * @return Cas-Repräsentation der Elemente der Rede
     */
    @Override
    public List<RedeElementCas> getElemente() {
        return this.redeElementCasList;
    }

    /**
     * @return Konvertiert die Rede zu einem Bson-Document
     */
    @Override
    public Document toDoc() throws SAXException {
        NlpDB nlpDB = new NlpDBImpl();

        Document document = new Document();
        document.append("_id", this.getRedeID());
        document.append("redeCas", nlpDB.serialiseJCas(this.jCas));

        List<Document> documentList = new ArrayList<>();

        for (RedeElementCas redeElementCas : redeElementCasList) {
            documentList.add(redeElementCas.toDoc());
        }

        document.append("redeElementCasList", documentList);

        return document;
    }
}
