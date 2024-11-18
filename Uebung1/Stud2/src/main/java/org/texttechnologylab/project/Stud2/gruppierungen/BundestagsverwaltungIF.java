package org.texttechnologylab.project.Stud2.gruppierungen;

import org.texttechnologylab.project.Stud2.impl.*;

public interface BundestagsverwaltungIF {
    public int btv_id = 0;
    public PersonalBundestagsverwaltung[ ] personal = null;

    // Factory-Method for Objects of Class 'Besuch'
    public Besuch besuch_planen(Gast[] gaeste, Abgeordneter[] abgeordnete, Raum[] raeume);

    // Factory-Method for Objects of Class 'Raumbelegung'
    Raumbelegung raumplan_festlegen(Besuch[] besuche, Raum raum);

    // Sets the budget of an 'Abgeordneter' - as asked in the text
    void set_budget_abgeordneter(Abgeordneter a, int budget);

    public int getBtv_id();

    public void setBtv_id(int btv_id);

    public PersonalBundestagsverwaltung[] getPersonal();

    public void setPersonal(PersonalBundestagsverwaltung[] personal);
}
