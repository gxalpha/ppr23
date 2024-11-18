package org.texttechnologylab.project.Stud2.data.impl.file;

import org.texttechnologylab.project.Stud2.data.Mandat;
import org.texttechnologylab.project.Stud2.data.Wahlperiode;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Klasse für eine Wahlperiode
 *
 * @author Stud2
 */
public class WahlperiodeImpl extends BundestagObjectImpl implements Wahlperiode {
    private final int number;
    private final Date startDate;
    private final Date endDate;
    private Set<Mandat> mandate;

    /**
     * Konstruktor für ein Objekt der Klasse Wahlperiode
     *
     * @param label der Bezeichner der Wahlperiode der Form "Wahlperiode" + number
     * @param number die Periodenzahl
     * @param startDate der Beginn der Wahlperiode
     * @param endDate das Ende der Wahlperiode
     */
    public WahlperiodeImpl(String label, int number, Date startDate, Date endDate) {
        super(label);
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mandate = new HashSet<>();
    }

    /**
     * @return Gibt die Nummer der Wahlperiode zurück
     */
    @Override
    public int getNumber() {
        return this.number;
    }

    /**
     * @return Gibt den Beginn der Wahlperiode zurück
     */
    @Override
    public Date getStartDate() {
        return this.startDate;
    }

    /**
     * @return Gibt das Ende der Wahlperiode zurück
     */
    @Override
    public Date getEndDate() { return this.endDate; }

    /**
     * @return Gibt die Mandate der Wahlperiode zurück
     */
    @Override
    public Set<Mandat> listMandate() {
        return this.mandate;
    }

    /**
     * @param typ der Typ des Mandates (Direktwahl/Landeswahl)
     * @return Gibt die Mandate der Wahlperiode nach Typ zurück
     */
    @Override
    public Set<Mandat> listMandate(Types.MANDAT typ) {
        Set<Mandat> result = new HashSet<>();

        for (Mandat m: this.mandate) {
            if (m.getTyp() != null) {
                if (m.getTyp().equals(typ)) {
                    result.add(m);
                }
            }
        }
        return result;
    }

    /**
     * Fügt zur Wahlperiode ein Mandat hinzu
     * @param m das hinzuzufügende Mandat
     */
    @Override
    public void addMandat(Mandat m){
        this.mandate.add(m);
    }

}
