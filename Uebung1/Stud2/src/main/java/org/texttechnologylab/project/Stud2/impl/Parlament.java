package org.texttechnologylab.project.Stud2.impl;

import org.texttechnologylab.project.Stud2.gruppierungen.ParlamentIF;

// A parliament consists of abgeordnete who are able to get together into fractions:
public class Parlament implements ParlamentIF {
    private int wahlperiode;
    private Abgeordneter[] abgeordnete;
    private Fraktion[] fraktionen;

    // Getter and Setter
    public int getWahlperiode() {
        return wahlperiode;
    }

    public void setWahlperiode(int wahlperiode) {
        this.wahlperiode = wahlperiode;
    }

    public Abgeordneter[] getAbgeordnete() {
        return abgeordnete;
    }

    public void setAbgeordnete(Abgeordneter[] abgeordnete) {
        this.abgeordnete = abgeordnete;
    }

    public Fraktion[] getFraktionen() {
        return fraktionen;
    }

    public void setFraktionen(Fraktion[] fraktionen) {
        this.fraktionen = fraktionen;
    }
}
