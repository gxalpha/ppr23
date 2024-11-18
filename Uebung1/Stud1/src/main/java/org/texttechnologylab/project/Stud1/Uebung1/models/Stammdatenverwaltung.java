package org.texttechnologylab.project.Stud1.Uebung1.models;

import org.texttechnologylab.project.Stud1.Uebung1.models.besuch.Besuch;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.Person;

/**
 * Haupt-Model.<br>
 * Enthält alle Personen und Besuche.
 * Speichert zudem die Email-Adresse der Verwaltung.
 */
public interface Stammdatenverwaltung {
    public String getBundestagsverwaltungEmail();

    public void setBundestagsverwaltungEmail(String email);

    public Besuch[] getBesuche();

    public void setBesuche(Besuch[] besuche);

    public Person[] getAllePersonen();

    public void setAllePersonen(Person[] personen);
}

class StammdatenverwaltungImpl implements Stammdatenverwaltung {
    private String bundestagsverwaltungEmail;
    private Besuch[] besuche;
    private Person[] personen;

    public StammdatenverwaltungImpl(String bundestagsverwaltungEmail, Besuch[] besuche, Person[] personen) {
        this.bundestagsverwaltungEmail = bundestagsverwaltungEmail;
        this.besuche = besuche;
        this.personen = personen;
    }

    @Override
    public String getBundestagsverwaltungEmail() {
        return bundestagsverwaltungEmail;
    }

    @Override
    public void setBundestagsverwaltungEmail(String bundestagsverwaltungEmail) {
        this.bundestagsverwaltungEmail = bundestagsverwaltungEmail;
    }

    @Override
    public Besuch[] getBesuche() {
        return besuche;
    }

    @Override
    public void setBesuche(Besuch[] besuche) {
        this.besuche = besuche;
    }

    @Override
    public Person[] getAllePersonen() {
        return personen;
    }

    @Override
    public void setAllePersonen(Person[] personen) {
        this.personen = personen;
    }
}
