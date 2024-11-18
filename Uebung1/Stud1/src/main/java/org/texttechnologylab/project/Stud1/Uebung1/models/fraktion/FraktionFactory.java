package org.texttechnologylab.project.Stud1.Uebung1.models.fraktion;

import org.texttechnologylab.project.Stud1.Uebung1.models.person.Abgeordneter;

public class FraktionFactory {
    private FraktionFactory() {
    }

    public static Fraktion makeFraktion(String name, Abgeordneter[] abgeordnete) {
        return new FraktionImpl(name, abgeordnete);
    }
}
