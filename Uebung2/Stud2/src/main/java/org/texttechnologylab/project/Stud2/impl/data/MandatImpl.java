package org.texttechnologylab.project.Stud2.impl.data;

import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Ausschuss;
import org.texttechnologylab.project.data.Fraktion;
import org.texttechnologylab.project.data.Mandat;
import org.texttechnologylab.project.data.Mitgliedschaft;
import org.texttechnologylab.project.data.Types;
import org.texttechnologylab.project.data.Wahlkreis;
import org.texttechnologylab.project.data.Wahlperiode;
import org.texttechnologylab.project.exception.BundestagException;
import org.texttechnologylab.project.Stud2.impl.exception.AttributeNotFoundError;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Klasse für ein Mandat
 *
 * @author Stud2
 */
public class MandatImpl extends BundestagObjectImpl implements Mandat {
    private final Abgeordneter abgeordneter;
    private final Date fromDate;
    private final Date toDate;
    private Set<Fraktion> fraktionen;
    private Set<Ausschuss> ausschuesse;
    private Set<Mitgliedschaft> mitgliedschaften;
    private final Types.MANDAT typ;
    private final Wahlperiode wahlperiode;
    private final Wahlkreis wahlkreis;

    /**
     * Konstruktor für ein Objekt der Klasse Mandat
     *
     * @param label der eindeutige Bezeichner des Mandats
     * @param abgeordneter der Abgeordnete
     * @param fromDate der Beginn des Mandats
     * @param toDate das Ende des Mandats
     * @param typ der Typ des Mandates (Direktwahl/Landesliste)
     * @param wahlperiode die Wahlperiode des Mandats
     * @param wahlkreis der Wahlkreis des Mandats
     */
    public MandatImpl(String label, Abgeordneter abgeordneter, Date fromDate, Date toDate, Types.MANDAT typ,
                      Wahlperiode wahlperiode, Wahlkreis wahlkreis) {
        super(label);
        this.abgeordneter = abgeordneter;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fraktionen = new HashSet<>();
        this.ausschuesse = new HashSet<>();
        this.mitgliedschaften = new HashSet<>(); // Enthält u.A. Fraktionen und Ausschüsse
        this.typ = typ;
        this.wahlperiode = wahlperiode;
        this.wahlkreis = wahlkreis;
    }

    /**
     * @return Gibt den Abgeordneten des Mandats zurück
     */
    @Override
    public Abgeordneter getAbgeordneter() {
        return this.abgeordneter;
    }

    /**
     * @return Gibt den Beginn des Mandates zurück
     */
    @Override
    public Date fromDate() {
        return this.fromDate;
    }

    /**
     * @return Gibt das Ende des Mandates zurück
     */
    @Override
    public Date toDate() {
        return this.toDate;
    }

    /**
     * @return Gibt eine Liste aller Fraktionen während des Mandates zurück
     */
    @Override
    public Set<Fraktion> getFraktionen() {
        return this.fraktionen;
    }

    /**
     * @return Gibt eine Liste aller Ausschüsse während des Mandates zurück
     */
    @Override
    public Set<Ausschuss> listAusschuesse() {
        return this.ausschuesse;
    }

    /**
     * @return Gibt alle Mitgliedschaften während des Mandates zurück
     */
    @Override
    public Set<Mitgliedschaft> listMitgliedschaft() {
        return this.mitgliedschaften;
    }

    /**
     * @return Gibt den Typen des Mandats zurück
     */
    @Override
    public Types.MANDAT getTyp() {
        return this.typ;
    }

    /**
     * @return Gibt die Wahlperiode während des Mandates zurück
     */
    @Override
    public Wahlperiode getWahlperiode() {
        return this.wahlperiode;
    }

    /**
     * @return Gibt den Wahlkreis des Mandates zurück
     * @throws BundestagException falls der Wahlkreis null ist
     */
    @Override
    public Wahlkreis getWahlkreis() throws BundestagException {
        if (this.wahlkreis == null) {
            throw new AttributeNotFoundError ("Diesem Mandat " + this.getLabel() + " besitzt kein Wahlkreisattribut.");
        }
        return this.wahlkreis;
    }

    /**
     * Fügt eine Fraktion zum Mandat hinzu
     *
     * @param f die hinzuzufügende Fraktion
     */
    public void addFraktion(Fraktion f){
        this.fraktionen.add(f);
    }

    /**
     * Fügt einen Ausschuss zum Mandat hinzu
     *
     * @param a der hinzuzufügende Ausschuss
     */
    public void addAusschuss(Ausschuss a){
        this.ausschuesse.add(a);
    }

    /**
     * Fügt eine Mitgliedschaft zum Mandat hinzu
     *
     * @param m die hinzuzufügende Mitgliedschaft
     */
    public void addMitgliedschaft(Mitgliedschaft m){
        this.mitgliedschaften.add(m);
    }

}
