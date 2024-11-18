package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Abstimmung;
import org.texttechnologylab.project.data.Wahlperiode;

/**
 * Abstimmung mit Datumsinformation
 */
public interface ZeitlicheAbstimmung extends Abstimmung {
    /**
     * Gibt die Wahlperiode der Abstimmung zurück
     *
     * @return Wahlperiode der Abstimmung
     */
    Wahlperiode getWahlperiode();
}
