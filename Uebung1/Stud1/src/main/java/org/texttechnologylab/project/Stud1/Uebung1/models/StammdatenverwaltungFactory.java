package org.texttechnologylab.project.Stud1.Uebung1.models;

import org.texttechnologylab.project.Stud1.Uebung1.models.besuch.Besuch;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.Person;

public class StammdatenverwaltungFactory {
    private StammdatenverwaltungFactory() {
    }

    public static Stammdatenverwaltung makeStammdatenverwaltung(String bundestagsverwaltungEmail, Besuch[] besuche, Person[] personen) {
        return new StammdatenverwaltungImpl(bundestagsverwaltungEmail, besuche, personen);
    }
}
