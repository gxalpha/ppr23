package org.texttechnologylab.project.Stud2.data.impl.file;

import org.texttechnologylab.project.Stud2.data.Abgeordneter;
import org.texttechnologylab.project.Stud2.data.Gruppe;
import org.texttechnologylab.project.Stud2.data.Mitgliedschaft;
import org.texttechnologylab.project.Stud2.data.Wahlperiode;
import org.texttechnologylab.project.Stud2.exceptions.AttributeNotFoundError;

import java.sql.Date;

/**
 * Klasse für eine Mitgliedschaft
 *
 * @author Stud2
 */
public class MitgliedschaftImpl implements Mitgliedschaft {
    private final Abgeordneter abgeordneter;
    private final String funktion;
    private final Date fromDate;
    private final Date toDate;
    private final Gruppe gruppe;
    private final Wahlperiode wahlperiode;

    /**
     * Konstruktor für ein Objekt der Klasse Mitgliedschaft //
     *
     * @param abgeordneter das Mitglied
     * @param funktion die Funktion des Mitgliedes in der Gruppe
     * @param fromDate der Beginn der Mitgliedschaft
     * @param toDate das Ende der Mitgliedschaft
     * @param gruppe die zugehörige Gruppe
     * @param wahlperiode die Wahlperiode der Mitgliedschaft
     */
    public MitgliedschaftImpl(Abgeordneter abgeordneter, String funktion, Date fromDate,
                              Date toDate, Gruppe gruppe, Wahlperiode wahlperiode) {

        this.abgeordneter = abgeordneter;
        this.funktion = funktion;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.gruppe = gruppe;
        this.wahlperiode = wahlperiode;
    }

    /**
     * @return Gibt den Abgeordneten zurück
     */
    @Override
    public Abgeordneter getAbgeordneter() {
        return this.abgeordneter;
    }

    /**
     * @return Gibt die Funktion in der Mitgliedschaft zurück
     */
    @Override
    public String getFunktion() throws AttributeNotFoundError {
        if (this.funktion == null) {
            throw new AttributeNotFoundError("Das Funktionsattribut des Abgeordneten in der Mitgliedschaft ist null.");
        }
        return this.funktion; }

    /**
     * @return Gibt den Beginn der Mitgliedschaft zurück
     */
    @Override
    public Date getFromDate() {
        return this.fromDate;
    }

    /**
     * @return Gibt das Ende der Mitgliedschaft zurück
     */
    @Override
    public Date getToDate() {
        return this.toDate;
    }

    /**
     * @return Gibt Aufschluss darüber, ob der Abgeordnete noch aktives Mitglied der Gruppe ist
     */
    @Override
    public boolean isActive() {
        Date currDate = new Date(System.currentTimeMillis());
        if (this.toDate == null && this.fromDate != null && this.getAbgeordneter().getSterbeDatum() == null){
            return true; // kein Enddatum bekannt und Abgeordneter lebt noch
        } else if (this.toDate != null && this.fromDate != null) {
            return this.fromDate.before(currDate) && this.toDate.after(currDate);
        }
        else {
            return false; // beide Zeiten null
        }
    }

    /**
     * @return Gibt die Gruppe der Mitgliedschaft zurück
     */
    @Override
    public Gruppe getGruppe() {
        return this.gruppe;
    }

    /**
     * @return Gibt die Wahlperiode der Mitgliedschaft zurück
     */
    @Override
    public Wahlperiode getWahlperiode() {
        return this.wahlperiode;
    }

}
