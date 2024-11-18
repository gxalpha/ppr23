package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Gruppe;
import org.texttechnologylab.project.data.Mitgliedschaft;
import org.texttechnologylab.project.data.Wahlperiode;

import java.util.HashSet;
import java.util.Set;

/**
 * Private Implementierung von Gruppe
 */
class GruppeImpl extends BundestagObjectImpl implements Gruppe {
    private final String name;
    private final Set<Abgeordneter> members;

    /**
     * @param name Name der Gruppe
     */
    GruppeImpl(String name) {
        this.name = name;
        this.members = new HashSet<>();
    }

    /**
     * Fügt einen Abgeordneten der Gruppe hinzu
     *
     * @param abgeordneter Der Abgeordnete
     */
    void addMember(Abgeordneter abgeordneter) {
        members.add(abgeordneter);
    }

    /**
     * @return Mitglieder der Gruppe
     */
    @Override
    public Set<Abgeordneter> getMembers() {
        return members;
    }

    /**
     * @param wahlperiode Wahlperiode
     * @return Mitglieder der Gruppe während der gegebenen Wahlperiode
     */
    @Override
    public Set<Abgeordneter> getMembers(Wahlperiode wahlperiode) {
        Set<Abgeordneter> passendeAbgeordnete = new HashSet<>();
        for (Abgeordneter abgeordneter : getMembers()) {
            for (Mitgliedschaft mitgliedschaft : abgeordneter.listMitgliedschaften(wahlperiode)) {
                if (mitgliedschaft.getGruppe().equals(this)) {
                    passendeAbgeordnete.add(abgeordneter);
                    break; // Inner loop
                }
            }
        }
        return passendeAbgeordnete;
    }

    /**
     * @return Name der Gruppe
     */
    @Override
    public String getLabel() {
        return name;
    }
}
