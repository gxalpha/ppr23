package org.texttechnologylab.project.Stud2.impl;

import org.texttechnologylab.project.Stud2.besuch.RaumIF;

// Objects of this class represent a room where visits can take place:
public class Raum implements RaumIF {
    private int raumID;
    private int kapazitaet;
    private Raumbelegung raumbelegung;

    // Getter and Setter
    public int getRaumID() {
        return raumID;
    }

    public int getKapazitaet() {
        return kapazitaet;
    }

    public void setKapazitaet(int kapazitaet) {
        this.kapazitaet = kapazitaet;
    }

    public Raumbelegung getRaumbelegung() {
        return raumbelegung;
    }

    public void setRaumbelegung(Raumbelegung raumbelegung) {
        this.raumbelegung = raumbelegung;
    }
}
