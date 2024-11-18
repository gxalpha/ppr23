package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Mandat;
import org.texttechnologylab.project.data.Wahlkreis;
import org.texttechnologylab.project.data.Wahlperiode;

import java.util.HashSet;
import java.util.Set;

/**
 * Private Implementierung von Wahlkreis
 */
class WahlkreisImpl extends BundestagObjectImpl implements Wahlkreis {
    private final String name;
    private final int number;
    private final Set<Mandat> mandate;

    /**
     * @param name   Name des Wahlkreises
     * @param number Wahlkreisnummer
     */
    WahlkreisImpl(String name, int number) {
        super();
        this.name = name;
        this.number = number;
        this.mandate = new HashSet<>();
    }

    /**
     * Fügt ein Mandat zu dem Wahlkreis hinzu
     *
     * @param mandat Das Mandat
     */
    void addMandat(Mandat mandat) {
        mandate.add(mandat);
    }

    /**
     * @return Nummer des Wahlkreises
     */
    @Override
    public int getNumber() {
        return number;
    }

    /**
     * @return Mandate des Wahlkreises
     */
    @Override
    public Set<Mandat> getMandate() {
        return mandate;
    }

    /**
     * @param wahlperiode Wahlperiode
     * @return Mandate des Wahlkreises während der Wahlperiode
     */
    @Override
    public Set<Mandat> getMandate(Wahlperiode wahlperiode) {
        Set<Mandat> passendeMandate = new HashSet<>();
        for (Mandat mandat : getMandate()) {
            if (wahlperiode.equals(mandat.getWahlperiode())) {
                passendeMandate.add(mandat);
            }
        }
        return passendeMandate;
    }

    /**
     * @return Name des Wahlkreises
     */
    @Override
    public String getLabel() {
        return name;
    }
}
