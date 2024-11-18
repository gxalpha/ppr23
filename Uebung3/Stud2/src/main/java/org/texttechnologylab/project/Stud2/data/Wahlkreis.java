package org.texttechnologylab.project.Stud2.data;

import java.util.Set;

/**
 * Ein Interface für einen Wahlkreis
 *
 * @author Stud2
 */
public interface Wahlkreis extends BundestagObject {
    /**
     * @return Gibt die Wahlkreisnummer zurück
     */
    int getNumber();

    /**
     * @return Gibt eine Liste aller Mandate zurück
     */
    Set<Mandat> getMandate();

    /**
     * @param wahlperiode die Wahlperiode, nach der gefiltert werden soll
     * @return Gibt eine Liste aller Mandate während einer Wahlperiode zurück
     */
    Set<Mandat> getMandate(Wahlperiode wahlperiode);

    /**
     * Fügt dem Wahlkreis ein Mandat hinzu
     *
     * @param m dan hinzuzufügende Mandat
     */
    void addMandat(Mandat m);
}
