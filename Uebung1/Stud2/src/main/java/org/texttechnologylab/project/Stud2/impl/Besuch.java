package org.texttechnologylab.project.Stud2.impl;

import org.texttechnologylab.project.Stud2.besuch.BesuchIF;

import java.sql.Time;
import java.util.Date;

// An object of this class represents a specific visit
public class Besuch implements BesuchIF {
    private int besuchsID;
    private Date besuchsdatum;
    private Time beginn;
    private Time ende;
    private Gast[] gaeste;
    private Raum raum;
    private Abgeordneter[] anwesende_abgeordnete;

    // Getter and Setter
    public int getBesuchsID() {
        return besuchsID;
    }

    public Date getBesuchsdatum() {
        return besuchsdatum;
    }

    public void setBesuchsdatum(Date besuchsdatum) {
        this.besuchsdatum = besuchsdatum;
    }

    public Time getBeginn() {
        return beginn;
    }

    public void setBeginn(Time beginn) {
        this.beginn = beginn;
    }

    public Time getEnde() {
        return ende;
    }

    public void setEnde(Time ende) {
        this.ende = ende;
    }

    public Gast[] getGaeste() {
        return gaeste;
    }

    public void setGaeste(Gast[] gaeste) {
        this.gaeste = gaeste;
    }

    public Raum getRaum() {
        return raum;
    }

    public void setRaum(Raum raum) {
        this.raum = raum;
    }

    public Abgeordneter[] getAnwesende_abgeordnete() {
        return anwesende_abgeordnete;
    }

    public void setAnwesende_abgeordnete(Abgeordneter[] anwesende_abgeordnete) {
        this.anwesende_abgeordnete = anwesende_abgeordnete;
    }
}
