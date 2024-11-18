package org.texttechnologylab.project.Stud2.impl.data;

import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Partei;
import org.texttechnologylab.project.data.Wahlperiode;

import java.util.Set;

/**
 * Klasse für eine Partei
 *
 * @author Stud2
 */
public class ParteiImpl extends GruppeImpl implements Partei {

    /**
     * Konstruktor für ein Objekt der Klasse Partei
     *
     * @param label die Abkürzung der Partei
     */
    public ParteiImpl(String label) {
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
