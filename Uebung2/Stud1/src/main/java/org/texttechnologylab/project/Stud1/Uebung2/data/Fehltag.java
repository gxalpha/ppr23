package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Abgeordneter;

import java.sql.Date;

/**
 * Interface für einen Fehltag
 */
public interface Fehltag {

    /**
     * Fehlender Abgeordneter
     */
    Abgeordneter getAbgeordneter();

    /**
     * Gibt das Datum des Fehltages zurück
     *
     * @return Datum des Fehltages
     */
    Date getDate();
}
