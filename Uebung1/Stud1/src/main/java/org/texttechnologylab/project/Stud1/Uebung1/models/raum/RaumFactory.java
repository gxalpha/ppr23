package org.texttechnologylab.project.Stud1.Uebung1.models.raum;

public class RaumFactory {
    private RaumFactory() {
    }

    public static Raum makeRaum(String raumbezeichung, int kapazitaet) {
        return new RaumImpl(raumbezeichung, kapazitaet);
    }
}
