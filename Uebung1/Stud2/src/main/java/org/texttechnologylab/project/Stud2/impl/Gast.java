package org.texttechnologylab.project.Stud2.impl;

import org.texttechnologylab.project.Stud2.personen.GastIF;

import java.util.Date;

// Represents a visitor of an Abgeordneter
public class Gast implements GastIF {
    private int gastID;
    private Date besuchsdatum;
    private Abgeordneter[] eingeladet_von;

    // Getter and Setter
    public int getGastID() {
        return gastID;
    }

    public void setGastID(int gastID) {
        this.gastID = gastID;
    }

    public Date getBesuchsdatum() {
        return besuchsdatum;
    }

    public void setBesuchsdatum(Date besuchsdatum) {
        this.besuchsdatum = besuchsdatum;
    }

    public Abgeordneter[] getEingeladet_von() {
        return eingeladet_von;
    }

    public void setEingeladet_von(Abgeordneter[] eingeladet_von) {
        this.eingeladet_von = eingeladet_von;
    }
}
