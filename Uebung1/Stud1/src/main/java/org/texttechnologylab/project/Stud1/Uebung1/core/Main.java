package org.texttechnologylab.project.Stud1.Uebung1.core;

import org.texttechnologylab.project.Stud1.Uebung1.models.person.Abgeordneter;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.PersonFactory;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.*;

public class Main {
    public static void main(String[] args) {
        Institution[] institutionen = new Institution[0];
        Wahlperiode[] wahlperioden = new Wahlperiode[0];
        Name[] namen = new Name[0];
        BiografischeAngaben biografischeAngaben = StammdatenFactory.makeBiografischeAngaben("", "", "", "", "", "", "", "", "", "", "");

        Abgeordneter abgeordneter = PersonFactory.makeAbgeordneter(institutionen, wahlperioden, namen, biografischeAngaben, "");
    }
}
