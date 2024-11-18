package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Mandat;
import org.texttechnologylab.project.data.Wahlperiode;

import java.util.HashSet;
import java.util.Set;

/**
 * Private Implementierung von Landesliste
 */
class LandeslisteImpl implements Landesliste {
    private final String kuerzel;
    private final Set<Mandat> mandate;

    /**
     * Default-Konstruktor der Landesliste
     *
     * @param kuerzel Kürzel der Liste
     */
    LandeslisteImpl(String kuerzel) {
        this.kuerzel = kuerzel;
        this.mandate = new HashSet<>();
    }

    /**
     * @param mandat Fügt der Liste ein Mandat hinzu
     */
    void addMandat(Mandat mandat) {
        mandate.add(mandat);
    }

    /**
     * @return Das Kürzel der Liste
     */
    @Override
    public String getKuerzel() {
        return kuerzel;
    }

    /**
     * Gibt alle Listenmandate der Liste in einer Wahlperiode zurück
     *
     * @param wahlperiode Wahlperiode
     * @return Menge der Mandate
     */
    @Override
    public Set<Mandat> getMandate(Wahlperiode wahlperiode) {
        return mandate;
    }
}
