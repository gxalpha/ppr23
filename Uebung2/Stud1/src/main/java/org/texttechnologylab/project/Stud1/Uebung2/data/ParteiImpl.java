package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Partei;
import org.texttechnologylab.project.data.Wahlperiode;

import java.util.Set;

/**
 * Private Implementierung von Partei
 */
class ParteiImpl extends GruppeImpl implements Partei {

    /**
     * @param label Name der Partei
     */
    ParteiImpl(String label) {
        super(label);
    }

    /**
     * @return Mitglieder der Partei
     */
    @Override
    public Set<Abgeordneter> listMitglieder() {
        return super.getMembers(); // This function is kinda redundant, but unfortunately still required
    }

    /**
     * @param wahlperiode Wahlperiode
     * @return Die Mitglieder der Partei während einer Wahlperiode
     */
    @Override
    public Set<Abgeordneter> listMitglieder(Wahlperiode wahlperiode) {
        return super.getMembers(wahlperiode);
    }
}
