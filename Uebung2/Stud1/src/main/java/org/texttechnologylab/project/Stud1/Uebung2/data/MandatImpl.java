package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.*;
import org.texttechnologylab.project.exception.BundestagException;

import java.sql.Date;
import java.util.Set;

/**
 * Private Implementierung von Mandat
 */
class MandatImpl extends BundestagObjectImpl implements Mandat {
    private final Abgeordneter abgeordneter;
    private final Date fromDate;
    private final Date toDate;
    private final Set<Fraktion> fraktionen;
    private final Set<Ausschuss> ausschuesse;
    private final Set<Mitgliedschaft> mitgliedschaften;
    private final Types.MANDAT typ;
    private final Wahlperiode wahlperiode;
    private final Wahlkreis wahlkreis;

    /**
     * @param abgeordneter     Abgeordneter
     * @param fromDate         Startdatum des Mandats
     * @param toDate           Enddatum des Mandats
     * @param fraktionen       Fraktionen des Mandats
     * @param ausschuesse      Ausschüsse
     * @param mitgliedschaften Mitgliedschaften
     * @param typ              Art des Mandats
     * @param wahlperiode      Wahlperiode des Mandats
     * @param wahlkreis        Wahlkreis des Mandats
     */
    MandatImpl(Abgeordneter abgeordneter, Date fromDate, Date toDate, Set<Fraktion> fraktionen, Set<Ausschuss> ausschuesse, Set<Mitgliedschaft> mitgliedschaften, Types.MANDAT typ, Wahlperiode wahlperiode, Wahlkreis wahlkreis) {
        super();
        this.abgeordneter = abgeordneter;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fraktionen = fraktionen;
        this.ausschuesse = ausschuesse;
        this.mitgliedschaften = mitgliedschaften;
        this.typ = typ;
        this.wahlperiode = wahlperiode;
        this.wahlkreis = wahlkreis;
    }

    /**
     * @return Abgeordneter
     */
    @Override
    public Abgeordneter getAbgeordneter() {
        return abgeordneter;
    }

    /**
     * @return Startdatum des Mandats
     */
    @Override
    public Date fromDate() {
        return fromDate;
    }

    /**
     * @return Enddatum des Mandats
     */
    @Override
    public Date toDate() {
        return toDate;
    }

    /**
     * @return Fraktionen, in denen der Abgeordnete ist
     */
    @Override
    public Set<Fraktion> getFraktionen() {
        return fraktionen;
    }

    /**
     * @return Ausschüsse, in denen der Abgeordnete während des Mandats ist
     */
    @Override
    public Set<Ausschuss> listAusschuesse() {
        return ausschuesse;
    }

    /**
     * @return Mitgliedschaften des Abgeordneten während des Mandats
     */
    @Override
    public Set<Mitgliedschaft> listMitgliedschaft() {
        return mitgliedschaften;
    }

    /**
     * @return Typ des Mandats
     */
    @Override
    public Types.MANDAT getTyp() {
        return typ;
    }

    /**
     * @return Wahlperiode des Mandats
     */
    @Override
    public Wahlperiode getWahlperiode() {
        return wahlperiode;
    }

    /**
     * @return Wahlkreis des Mandats
     * @throws BundestagException Wenn der Abgeordnete nicht über ein Direktmandat im Bundestag ist
     */
    @Override
    public Wahlkreis getWahlkreis() throws BundestagException {
        if (wahlkreis == null) {
            throw new BundestagException("Mandat nicht durch Wahlkreis");
        } else {
            return wahlkreis;
        }
    }

    /**
     * @return Kurzbeschreibung des Mandats
     */
    @Override
    public String getLabel() {
        String mandatsart;
        if (typ == null) {
            mandatsart = "Unbekannte Mandatsart";
        } else {
            switch (typ) {
                case DIREKTWAHL:
                    mandatsart = "Direktmandat";
                    break;
                case LANDESLISTE:
                    mandatsart = "Landeslistenmandat";
                    break;
                default:
                    throw new RuntimeException("Unbekannte Mandatsart '" + typ + "'");
            }
        }
        return mandatsart + " von " + abgeordneter.getLabel() + " in " + wahlperiode.getLabel();
    }
}
