package org.texttechnologylab.project.Stud2.data.impl;

import org.texttechnologylab.project.Stud2.data.Abgeordneter;
import org.texttechnologylab.project.Stud2.data.Partei;
import org.texttechnologylab.project.Stud2.data.Wahlperiode;

import java.util.Set;

/**
 * Klasse für eine Partei
 *
 * @author Stud2
 */
public class Partei_Impl extends Gruppe_Impl implements Partei {

    /**
     * Konstruktor für ein Objekt der Klasse Partei
     *
     * @param label die Abkürzung der Partei
     */
    public Partei_Impl(String label) {
        super(label);
    }

    /**
     * @return Gibt eine Liste aller Mitglieder der Partei zurück
     */
    @Override
    public Set<Abgeordneter> listMitglieder() {
        return super.getMembers();
    }

    /**
     * @param wahlperiode die Wahlperiode, nach der gefiltert werden soll
     * @return Gibt eine Liste aller Mitglieder der Partei einer Wahlperiode zurück
     */
    @Override
    public Set<Abgeordneter> listMitglieder(Wahlperiode wahlperiode) {
        return super.getMembers(wahlperiode);
    }

}
