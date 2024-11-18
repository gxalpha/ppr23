package org.texttechnologylab.project.Stud2.data.impl;

import org.texttechnologylab.project.Stud2.data.Abgeordneter;
import org.texttechnologylab.project.Stud2.data.Fraktion;

import java.util.HashMap;
import java.util.Map;

/**
 * Klasse für eine Fraktion
 *
 * @author Stud2
 */
public class Fraktion_Impl extends Gruppe_Impl implements Fraktion {
    private Map<String, Abgeordneter> funktionstraeger;

    /**
     * Konstruktor für ein Objekt der Klasse Fraktion
     *
     * @param label Der Name der Fraktion
     */
    public Fraktion_Impl(String label) {
        super(label);
        this.funktionstraeger = new HashMap<>();
    }

    /**
     * Fügt ein Funktion-Funktionsträger-Paar zur Fraktion hinzu
     * @param funktion Die Funktion in der Fraktion - enthält die Wahlperiode für die Eindeutigkeit des Schlüssels
     * @param a Der Funktionsträger (Abgeordnete)
     */
    @Override
    public void addFunktionstraeger(String funktion, Abgeordneter a){
        this.funktionstraeger.put(funktion, a);
    }

    /**
     * @return Gibt alle Funktionsträger der Fraktion zurück
     */
    @Override
    public Map<String, Abgeordneter> getFunktionaere() {
        return this.funktionstraeger;
    }

}
