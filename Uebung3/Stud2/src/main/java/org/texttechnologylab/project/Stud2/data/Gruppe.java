package org.texttechnologylab.project.Stud2.data;

import java.util.Set;

/**
 * Ein Interface für eine Gruppe
 *
 * @author Stud2
 */
public interface Gruppe extends BundestagObject {
    /**
     * Fügt einen Abgeordneten zu einer Gruppe hinzu
     *
     * @param abgeordneter Der hinzuzufügende Abgeordnete
     */
    void addMember(Abgeordneter abgeordneter);

    /**
     * @return eine Liste aller Mitglieder
     */
    Set<Abgeordneter> getMembers();

    /**
     * @param wahlperiode die Wahlperiode. nach der gefiltert werden soll
     * @return Gibt eine Liste aller Mitglieder gemäß Wahlperiode zurück
     */
    Set<Abgeordneter> getMembers(Wahlperiode wahlperiode);
}
