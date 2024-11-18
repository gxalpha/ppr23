package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Mandat;
import org.texttechnologylab.project.data.Wahlperiode;

import java.util.Set;

/**
 * Interface einer Landesliste
 */
public interface Landesliste {

    /**
     * Gibt das Kürzel der Landesliste zurück
     *
     * @return Das Kürzel der Liste
     */
    String getKuerzel();

    /**
     * Gibt alle Listenmandate der Liste in einer Wahlperiode zurück
     *
     * @param wahlperiode Die Wahlperiode
     * @return Menge der Mandate
     */
    Set<Mandat> getMandate(Wahlperiode wahlperiode);
}
