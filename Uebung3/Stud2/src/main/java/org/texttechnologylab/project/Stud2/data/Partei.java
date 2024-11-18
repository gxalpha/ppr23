package org.texttechnologylab.project.Stud2.data;

import java.util.Set;

/**
 * Ein Interface für eine Partei
 *
 * @author Stud2
 */
public interface Partei extends Gruppe {
    /**
     * @return Gibt eine Liste aller Mitglieder der Partei zurück
     */
    Set<Abgeordneter> listMitglieder();

    /**
     * @param wahlperiode die Wahlperiode, nach der gefiltert werden soll
     * @return Gibt eine Liste aller Mitglieder der Partei einer Wahlperiode zurück
     */
    Set<Abgeordneter> listMitglieder(Wahlperiode wahlperiode);
}
