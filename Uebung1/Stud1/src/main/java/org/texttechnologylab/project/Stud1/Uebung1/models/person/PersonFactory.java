package org.texttechnologylab.project.Stud1.Uebung1.models.person;

import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.BiografischeAngaben;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.Institution;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.Name;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.Wahlperiode;

public class PersonFactory {

    private PersonFactory() {
    }

    public static Abgeordneter makeAbgeordneter(Institution[] institutionen, Wahlperiode[] wahlperioden, Name[] namen, BiografischeAngaben biografischeAngaben, String email) {
        return new AbgeordneterImpl(institutionen, wahlperioden, namen, biografischeAngaben, email);
    }

    public static Mitarbeiter makeMitarbeiter(Abgeordneter abgeordneter, int beschaeftigungsumfang, Name[] namen, BiografischeAngaben biografischeAngaben, String email) {
        return new MitarbeiterImpl(abgeordneter, beschaeftigungsumfang, namen, biografischeAngaben, email);
    }

    public static Besucher makeBesucher(Name[] namen, BiografischeAngaben biografischeAngaben, String email) {
        return new BesucherImpl(namen, biografischeAngaben, email);
    }
}
