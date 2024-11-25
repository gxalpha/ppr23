package org.texttechnologylab.project.Parliament_Browser_09_2.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Rede;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Tagesordnungspunkt;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.MongoDBHandlerImpl;

import java.io.IOException;
import java.util.List;

/**
 * Ein Tagesordnungspunkt.
 *
 * @author Stud
 */
public class TagesordnungspunktImpl implements Tagesordnungspunkt {

    private final String thema;
    private final List<String> redenIDs;

    /**
     * Konstruktor
     *
     * @param thema    Thema des Tagesordnungspunktes
     * @param redenIDs IDs der gehaltenen Reden während des TOPs
     */
    public TagesordnungspunktImpl(String thema, List<String> redenIDs) {
        this.thema = thema;
        this.redenIDs = redenIDs;
    }

    public TagesordnungspunktImpl(Document document) {
        this.thema = document.getString("thema");
        this.redenIDs = document.getList("redenIDs", String.class);
    }

    /**
     * @return Thema des Tagesordnungspunktes
     */
    @Override
    public String getThema() {
        return this.thema;
    }

    /**
     * @return IDs der Reden des Tagesordnungspunktes
     */
    @Override
    public List<String> getRedenIDs() {
        return this.redenIDs;
    }

    /**
     * @return Objekt in ihrer MongoDB-Repräsentation
     */
    @Override
    public Document toDoc() {
        Document document = new Document();
        document.put("thema", this.getThema());
        document.put("redenIDs", this.getRedenIDs());
        return document;
    }

    /**
     * Tagesordnungspunkt zu Tex-Code
     *
     * @return String mit Tex-Code für den Tagesordnungspunkt
     * @author Stud
     */
    @Override
    public String toTeX() throws IOException {
        String texCode = "";

        texCode += "\\section{" + this.thema + "}\n\n";

        MongoDBHandler mongoDBHandler = new MongoDBHandlerImpl("Project_09_02.txt");

        for (String redeId : redenIDs) {
            Rede rede = mongoDBHandler.getRedeByID(redeId);
            try {
                texCode += rede.toLaTeX();
            } catch (Exception e) {
                System.out.println(redeId);
            }

            texCode += "\n\n";
        }

        return texCode;
    }
}
