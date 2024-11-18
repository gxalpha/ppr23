package org.texttechnologylab.project.Stud2.data;

import java.sql.Date;
import java.util.Set;

/**
 * Ein Interface für eine Wahlperiode
 *
 * @author Stud2
 */
public interface Wahlperiode extends BundestagObject {
    /**
     * @return Gibt die Nummer der Wahlperiode zurück
     */
    int getNumber();

    /**
     * @return Gibt den Beginn der Wahlperiode zurück
     */
    Date getStartDate();

    /**
     * @return Gibt das Ende der Wahlperiode zurück
     */
    Date getEndDate();

    /**
     * @return Gibt die Mandate der Wahlperiode zurück
     */
    Set<Mandat> listMandate();

    /**
     * @param typ der Typ des Mandates (Direktwahl/Landeswahl)
     * @return Gibt die Mandate der Wahlperiode nach Typ zurück
     */
    Set<Mandat> listMandate(Types.MANDAT typ);

    /**
     * Fügt zur Wahlperiode ein Mandat hinzu
     *
     * @param m das hinzuzufügende Mandat
     */
    void addMandat(Mandat m);
}
