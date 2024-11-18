package org.texttechnologylab.project.Stud1.Uebung1.models.besuch;

import org.texttechnologylab.project.Stud1.Uebung1.models.person.Abgeordneter;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.Besucher;
import org.texttechnologylab.project.Stud1.Uebung1.models.raum.Raum;

public class BesuchFactory {
    private BesuchFactory() {
    }

    public static Besuch makeBesuch(Besucher[] besucher, Abgeordneter[] einladendeAbgeordnete, Raum raum) {
        return new BesuchImpl(besucher, einladendeAbgeordnete, raum);
    }
}
