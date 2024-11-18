package org.texttechnologylab.project.Stud2.data;

import java.util.Map;

/**
 * Ein Interface für eine Fraktion
 *
 * @author Stud2
 */
public interface Fraktion extends Gruppe {
    /**
     * Fügt ein Funktion-Funktionsträger-Paar zur Fraktion hinzu
     *
     * @param funktion Die Funktion in der Fraktion - enthält die Wahlperiode für die Eindeutigkeit des Schlüssels
     * @param a        Der Funktionsträger (Abgeordnete)
     */
    void addFunktionstraeger(String funktion, Abgeordneter a);

    /**
     * @return Gibt alle Funktionsträger der Fraktion zurück
     */
    Map<String, Abgeordneter> getFunktionaere();
}
