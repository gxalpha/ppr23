package org.texttechnologylab.project.Stud2.impl;

import org.texttechnologylab.project.Stud2.personen.PersonalBundestagsverwaltungIF;

import java.util.Date;

// Objects of this class represent a worker of the Bundestagsverwaltung
public class PersonalBundestagsverwaltung implements PersonalBundestagsverwaltungIF {
    private int personalID;
    private Date vertrag_von;
    private Date vertrag_bis;

    // This person is able to send a Raumbelegung to an Abgeordneter and his Mitarbeiter
    public void raumplan_senden(Abgeordneter a){}

    public int getPersonalID() {
        return personalID;
    }

    public void setPersonalID(int personalID) {
        this.personalID = personalID;
    }

    public Date getVertrag_von() {
        return vertrag_von;
    }

    public void setVertrag_von(Date vertrag_von) {
        this.vertrag_von = vertrag_von;
    }

    public Date getVertrag_bis() {
        return vertrag_bis;
    }

    public void setVertrag_bis(Date vertrag_bis) {
        this.vertrag_bis = vertrag_bis;
    }
}
