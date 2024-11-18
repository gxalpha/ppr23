package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Mandat;
import org.texttechnologylab.project.data.Types;
import org.texttechnologylab.project.data.Wahlperiode;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Private Implementierung von Wahlperiode
 */
class WahlperiodeImpl extends BundestagObjectImpl implements Wahlperiode {
    private final int number;
    private final Set<Mandat> mandate;
    private Date startDate;
    private Date endDate;

    /**
     * @param number    Nummer der Wahlperiode
     * @param startDate Startdatum
     * @param endDate   Enddatum
     */
    WahlperiodeImpl(int number, Date startDate, Date endDate) {
        super();
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mandate = new HashSet<>();
    }

    /**
     * Fügt ein Mandat der Wahlperiode hinzu
     *
     * @param mandat das Mandat
     */
    void addMandat(Mandat mandat) {
        mandate.add(mandat);
    }

    /**
     * @return Nummer der Wahlperiode
     */
    @Override
    public int getNumber() {
        return number;
    }

    /**
     * @return Beginn der Wahlperiode
     */
    @Override
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Setzt ein neues Startdatum der Wahlperiode
     *
     * @param startDate Neues Startdatum
     */
    void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return Ende der Wahlperiode
     */
    @Override
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Setzt ein neues Enddatum der Wahlperiode
     *
     * @param endDate Neues Enddatum
     */
    void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return Liste der Mandate während der Wahlperiode
     */
    @Override
    public Set<Mandat> listMandate() {
        return mandate;
    }

    /**
     * @param typ Art des Mandats
     * @return Liste der Mandate des gegebenen Typs während der Wahlperiode
     */
    @Override
    public Set<Mandat> listMandate(Types.MANDAT typ) {
        Set<Mandat> passendeMandate = new HashSet<>();
        for (Mandat mandat : listMandate()) {
            if (typ == mandat.getTyp()) {
                passendeMandate.add(mandat);
            }
        }
        return passendeMandate;
    }

    /**
     * @return Kurzbezeichnung der Wahlperiode
     */
    @Override
    public String getLabel() {
        return "WP " + number;
    }
}
