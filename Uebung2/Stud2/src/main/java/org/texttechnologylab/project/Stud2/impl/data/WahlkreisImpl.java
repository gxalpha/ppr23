package org.texttechnologylab.project.Stud2.impl.data;

import org.texttechnologylab.project.data.Mandat;
import org.texttechnologylab.project.data.Wahlkreis;
import org.texttechnologylab.project.data.Wahlperiode;

import java.util.HashSet;
import java.util.Set;

/**
 * Klasse für einen Wahlkreis
 *
 * @author Stud2
 */
public class WahlkreisImpl extends BundestagObjectImpl implements Wahlkreis {
    private final int number;
    private Set<Mandat> mandate;

    /**
     * Konstruktor für ein Objekt der Klasse Wahlkreis
     *
     * @param label ein Bezeichner der Form "Wahlkreis" + number
     * @param number die Wahlkreisnummer
     */
    public WahlkreisImpl(String label, int number) {
        super(label);
        this.number = number;
        this.mandate = new HashSet<>();
    }

    /**
     * @return Gibt die Wahlkreisnummer zurück
     */
    @Override
    public int getNumber() {
        return this.number;
    }

    /**
     * @return Gibt eine Liste aller Mandate zurück
     */
    @Override
    public Set<Mandat> getMandate() {
        return this.mandate;
    }

    /**
     * @param wahlperiode die Wahlperiode, nach der gefiltert werden soll
     * @return Gibt eine Liste aller Mandate während einer Wahlperiode zurück
     */
    @Override
    public Set<Mandat> getMandate(Wahlperiode wahlperiode) {
        HashSet<Mandat> result = new HashSet<>();
        for (Mandat mandat : this.mandate) {
                if (mandat.getWahlperiode().getNumber() == wahlperiode.getNumber()) {
                    result.add(mandat);
                }
        }
        return result;
    }

    /**
     * Fügt dem Wahlkreis ein Mandat hinzu
     *
     * @param m dan hinzuzufügende Mandat
     */
    public void addMandat(Mandat m){
        this.mandate.add(m);
    }

}
