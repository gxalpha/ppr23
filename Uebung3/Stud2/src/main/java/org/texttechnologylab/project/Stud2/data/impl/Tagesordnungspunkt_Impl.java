package org.texttechnologylab.project.Stud2.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.data.Rede;
import org.texttechnologylab.project.Stud2.data.Tagesordnungspunkt;
import org.texttechnologylab.project.Stud2.exception.RedeNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Eine Klasse für einen Tagesordnungspunkt (inklusive Zusatzpunkten und Einzelplänen)
 *
 * @author Stud2
 */
public class Tagesordnungspunkt_Impl extends BundestagObject_Impl implements Tagesordnungspunkt {
    private String angelegenheit;
    private final List<Rede> reden;

    /**
     * Der Konstruktor für ein Objekt dieser Klasse
     *
     * @param label das Label des Tagesordnungspunktes
     * @param reden die gehaltenen Reden in diesem Tagesordnungspunkt
     */
    public Tagesordnungspunkt_Impl(String label, List<Rede> reden) {
        super(label);
        this.reden = reden;
    }

    /**
     * Gibt die Rede mit der gegebenen ID zurück
     *
     * @param ID die ID der zurückzugebenden Rede
     * @return die Rede mit der übergebenen ID
     * @throws RedeNotFoundException falls die Rede mit der ID nicht existiert
     */
    @Override
    public Rede getRedeByID(int ID) throws RedeNotFoundException {
        for (Rede rede : this.listReden()) {
            if (rede.getID().equals(ID)) {
                return rede;
            }
        }
        throw new RedeNotFoundException ("Es existiert keine Rede mit der ID " + ID);
    }

    /**
     * @param angelegenheit das Thema des Tagesordnungspunktes
     */
    @Override
    public void setAngelegenheit(String angelegenheit) {
        this.angelegenheit = angelegenheit;
    }

    /**
     * @return das Thema des Tagesordnungspunktes
     */
    @Override
    public String getAngelegenheit() {
        return this.angelegenheit;
    }

    /**
     * @return die Reden, die in diesem Tagesordnungspunkt gehalten wurden
     */
    @Override
    public List<Rede> listReden() {
        return this.reden;
    }

    /**
     * @return der Tagesordnungspunkt als Dokument
     */
    @Override
    public Document toDoc() {
        Document document = new Document();
        document.append("_id", this.getID().toString());
        document.append("angelegenheit", this.getAngelegenheit());

        List<Integer> reden = new ArrayList<>();

        for (Rede rede : this.listReden()) {
            reden.add((int) rede.getID());
        }
        document.append("reden", reden);
        return document;
    }
}
