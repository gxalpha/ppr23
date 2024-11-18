package org.texttechnologylab.project.Stud2.impl;

import org.texttechnologylab.project.Stud2.besuch.RaumbelegungIF;

import java.util.Date;

// Represents a plan for a room:
public class Raumbelegung implements RaumbelegungIF{
    private Raum raum;
    private Besuch[] belegungen;
    private Date stand;

    // Getter and Setter
    public Raum getRaum() {
        return raum;
    }

    public void setRaum(Raum raum) {
        this.raum = raum;
    }

    public Besuch[] getBelegungen() {
        return belegungen;
    }

    public void setBelegungen(Besuch[] belegungen) {
        this.belegungen = belegungen;
    }

    public Date getStand() {
        return stand;
    }

    public void setStand(Date stand) {
        this.stand = stand;
    }
}
