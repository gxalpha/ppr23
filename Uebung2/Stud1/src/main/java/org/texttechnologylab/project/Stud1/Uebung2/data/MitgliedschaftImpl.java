package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Gruppe;
import org.texttechnologylab.project.data.Mitgliedschaft;
import org.texttechnologylab.project.data.Wahlperiode;
import org.texttechnologylab.project.exception.BundestagException;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Private Implementierung von Mitgliedschaft
 */
class MitgliedschaftImpl implements Mitgliedschaft {
    private final Abgeordneter abgeordneter;
    private final String funktion;
    private final Date fromDate;
    private final Date toDate;
    private final Gruppe gruppe;
    private final Wahlperiode wahlperiode;

    /**
     * @param abgeordneter Abgeordneter
     * @param funktion     Funktion des Abgeordneten
     * @param fromDate     Startdatum
     * @param toDate       Enddatum
     * @param gruppe       Gruppe der Mitgliedschaft
     * @param wahlperiode  Wahlperiode der Mitgliedschaft
     */
    MitgliedschaftImpl(Abgeordneter abgeordneter, String funktion, Date fromDate, Date toDate, Gruppe gruppe, Wahlperiode wahlperiode) {
        this.abgeordneter = abgeordneter;
        this.funktion = funktion;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.gruppe = gruppe;
        this.wahlperiode = wahlperiode;
    }

    /**
     * @return Abgeordneter
     */
    @Override
    public Abgeordneter getAbgeordneter() {
        return abgeordneter;
    }

    /**
     * @return Funktion der Mitgliedschaft
     * @throws BundestagException Wenn die Mitgliedschaft funktionslos ist
     */
    @Override
    public String getFunktion() throws BundestagException {
        if (funktion == null) {
            throw new BundestagException("Abgeordneter hat keine Funktion");
        } else {
            return funktion;
        }
    }

    /**
     * @return Start der Mitgliedschaft
     */
    @Override
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * @return Ende der Mitgliedschaft
     */
    @Override
    public Date getToDate() {
        return toDate;
    }

    /**
     * @return Ob die Mitgliedschaft aktiv ist
     */
    @Override
    public boolean isActive() {
        Date endDate;
        if (toDate == null && wahlperiode.getEndDate() == null) {
            return true;
        } else if (toDate == null) {
            endDate = wahlperiode.getEndDate();
        } else {
            endDate = toDate;
        }
        Date today = Date.valueOf(LocalDate.now());
        return today.before(endDate);
    }

    /**
     * @return Die Gruppe der Mitgliedschaft
     */
    @Override
    public Gruppe getGruppe() {
        return gruppe;
    }

    /**
     * @return Wahlperiode der Mitgliedschaft
     */
    @Override
    public Wahlperiode getWahlperiode() {
        return wahlperiode;
    }
}
