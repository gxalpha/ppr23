package org.texttechnologylab.project.Stud2.impl;

import org.texttechnologylab.project.Stud2.gruppierungen.BundestagsverwaltungIF;

public class Bundestagsverwaltung implements BundestagsverwaltungIF {
    private int btv_id;
    private PersonalBundestagsverwaltung[ ] personal;

    // Factory-Method for Objects of Class 'Besuch'
    public Besuch besuch_planen(Gast[] gaeste, Abgeordneter[] abgeordnete, Raum[] raeume){
        return null;
    }

    // Factory-Method for Objects of Class 'Raumbelegung'
    public Raumbelegung raumplan_festlegen(Besuch[] besuche, Raum raum){
        return null;
    }

    // Sets the budget of an 'Abgeordneter' - as asked in the text
    public void set_budget_abgeordneter(Abgeordneter a, int budget){}

    public int getBtv_id() {
        return btv_id;
    }

    public void setBtv_id(int btv_id) {
        this.btv_id = btv_id;
    }

    public PersonalBundestagsverwaltung[] getPersonal() {
        return personal;
    }

    public void setPersonal(PersonalBundestagsverwaltung[] personal) {
        this.personal = personal;
    }
}