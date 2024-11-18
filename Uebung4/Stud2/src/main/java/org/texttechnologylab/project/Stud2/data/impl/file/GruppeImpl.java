package org.texttechnologylab.project.Stud2.data.impl.file;

import org.texttechnologylab.project.Stud2.data.Abgeordneter;
import org.texttechnologylab.project.Stud2.data.Gruppe;
import org.texttechnologylab.project.Stud2.data.Mitgliedschaft;
import org.texttechnologylab.project.Stud2.data.Wahlperiode;

import java.util.HashSet;
import java.util.Set;

/**
 * Klasse für eine Gruppe
 *
 * @author Stud2
 */
public class GruppeImpl extends BundestagObjectImpl implements Gruppe {
    private Set<Abgeordneter> members;

    /**
     * Konstruktor für ein Objekt der Klasse Gruppe
     *
     * @param label Der eindeutige Bezeichner der Gruppe
     */
    public GruppeImpl(String label) {
        super(label);
        this.members = new HashSet<>();
    }

    /**
     * Fügt einen Abgeordneten zu einer Gruppe hinzu
     *
     * @param abgeordneter Der hinzuzufügende Abgeordnete
     */
    @Override
    public void addMember(Abgeordneter abgeordneter){
        this.members.add(abgeordneter);
    }

    /**
     * @return eine Liste aller Mitglieder
     */
    @Override
    public Set<Abgeordneter> getMembers() { return this.members; }

    /**
     * @param wahlperiode die Wahlperiode. nach der gefiltert werden soll
     * @return Gibt eine Liste aller Mitglieder gemäß Wahlperiode zurück
     */
    @Override
    public Set<Abgeordneter> getMembers(Wahlperiode wahlperiode) {
        HashSet<Abgeordneter> result = new HashSet<>();
        for (Abgeordneter a : this.getMembers()) {
            for (Mitgliedschaft m : a.listMitgliedschaften(wahlperiode)) {
                if (this.getLabel().equals(m.getGruppe().getLabel())) {
                    result.add(a);
                }
            }
        }
        return result;
    }
}
