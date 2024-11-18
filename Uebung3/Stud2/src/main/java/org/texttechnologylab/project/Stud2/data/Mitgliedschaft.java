package org.texttechnologylab.project.Stud2.data;

import org.texttechnologylab.project.Stud2.exception.AttributeNotFoundError;
import org.texttechnologylab.project.Stud2.exception.BundestagException;

import java.sql.Date;

/**
 * Ein Interface für eine Mitgliedschaft
 *
 * @author Stud2
 */
public interface Mitgliedschaft {
    /**
     * @return Gibt den Abgeordneten zurück
     */
    Abgeordneter getAbgeordneter();

    /**
     * @return Gibt die Funktion in der Mitgliedschaft zurück
     * @throws BundestagException falls die Funktion unbekannt ist
     */
    String getFunktion() throws BundestagException, AttributeNotFoundError;

    /**
     * @return Gibt den Beginn der Mitgliedschaft zurück
     */
    Date getFromDate();

    /**
     * @return Gibt das Ende der Mitgliedschaft zurück
     */
    Date getToDate();

    /**
     * @return Gibt Aufschluss darüber, ob der Abgeordnete noch aktives Mitglied der Gruppe ist
     */
    boolean isActive();

    /**
     * @return Gibt die Gruppe der Mitgliedschaft zurück
     */
    Gruppe getGruppe();

    /**
     * @return Gibt die Wahlperiode der Mitgliedschaft zurück
     */
    Wahlperiode getWahlperiode();
}
