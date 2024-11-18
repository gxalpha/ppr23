package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Abgeordneter;

import java.sql.Date;

/**
 * Private Implementierung des Fehltag-Interfaces
 */
class FehltagImpl implements Fehltag {
    private final Abgeordneter abgeordneter;
    private final Date date;

    /**
     * @param date         Datum des Fehltags
     * @param abgeordneter Fehlender Abgeordneter
     */
    public FehltagImpl(Date date, Abgeordneter abgeordneter) {
        this.date = date;
        this.abgeordneter = abgeordneter;
    }

    /**
     * Fehlender Abgeordneter
     */
    @Override
    public Abgeordneter getAbgeordneter() {
        return abgeordneter;
    }

    /**
     * @return Datum des Fehltags
     */
    @Override
    public Date getDate() {
        return date;
    }
}
