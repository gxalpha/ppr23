package org.texttechnologylab.project.Stud2.impl.data;

import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Abstimmung;
import org.texttechnologylab.project.data.Mandat;
import org.texttechnologylab.project.data.Mitgliedschaft;
import org.texttechnologylab.project.data.Partei;
import org.texttechnologylab.project.data.Types;
import org.texttechnologylab.project.data.Wahlperiode;
import org.texttechnologylab.project.exception.BundestagException;
import org.texttechnologylab.project.Stud2.impl.exception.AttributeNotFoundError;
import org.texttechnologylab.project.Stud2.impl.helper.StringFunctions;

import java.sql.Date;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Klasse für einen Abgeordneten
 *
 * @author Stud2
 */
public class AbgeordneterImpl extends BundestagObjectImpl implements Abgeordneter {
    private final int ID;
    private final String name;
    private final String vorname;
    private final String ortszusatz;
    private final String adelssuffix;
    private final String anrede;
    private final String akadTitel;
    private final Date geburtsdatum;
    private final String geburtsort;
    private final Date sterbedatum;
    private final Types.GESCHLECHT geschlecht;
    private final String religion;
    private final String beruf;
    private final String vita;
    private Set<Mandat> mandate;
    private Set<Mitgliedschaft> mitgliedschaften;
    private Set<Abstimmung> abstimmungen;
    private final Partei partei;

    /**
     * Konstruktor für ein Objekt der Klasse Abgeordneter
     *
     * @param name der Nachname des Abgeordneten
     * @param vorname der Vorname des Abgeordneten
     * @param ortszusatz der Ortszusatz
     * @param adelssuffix der Adelssuffix
     * @param anrede die Anrede
     * @param akadTitel der akademische Titel
     * @param geburtsdatum das Geburtsdatum
     * @param geburtsort der Geburtsort
     * @param sterbedatum das Sterbedatum
     * @param geschlecht das Geschlecht (männlich/weiblich)
     * @param religion die Religionszugehörigkeit
     * @param beruf der Beruf
     * @param vita eine kurze Vita des Abgeordneten
     * @param partei die Parteizugehörigkeit des Abgeordneten
     */
    public AbgeordneterImpl(int ID, String name, String vorname, String ortszusatz, String adelssuffix, String anrede,
                            String akadTitel, Date geburtsdatum, String geburtsort, Date sterbedatum,
                            Types.GESCHLECHT geschlecht, String religion, String beruf, String vita, Partei partei) {
        super("Abgeordneter: " + ID);
        this.ID = ID;
        this.name = name;
        this.vorname = vorname;
        this.ortszusatz = ortszusatz;
        this.adelssuffix = adelssuffix;
        this.anrede = anrede;
        this.akadTitel = akadTitel;
        this.geburtsdatum = geburtsdatum;
        this.geburtsort = geburtsort;
        this.sterbedatum = sterbedatum;
        this.geschlecht = geschlecht;
        this.religion = religion;
        this.beruf = beruf;
        this.vita = vita;
        this.mandate = new HashSet<>();
        this.mitgliedschaften = new HashSet<>();
        this.abstimmungen = new HashSet<>();
        this.partei = partei;
    }

    /**
     * @return die ID des Abgeordneten
     */
    @Override
    public Object getID() {
        return this.ID;
    }

    /**
     * @return der Name des Abgeordneten
     */
    @Override
    public String getName() { return this.name; }

    /**
     * @return der Vorname des Abgeordneten
     */
    @Override
    public String getVorname() { return this.vorname; }

    /**
     * @return der Ortszusatz des Abgeordneten
     */
    @Override
    public String getOrtszusatz() {
        return this.ortszusatz;
    }

    /**
     * @return der Adelszusatz des Abgeordneten
     */
    @Override
    public String getAdelssuffix() {
        return this.adelssuffix;
    }

    /**
     * @return die Anrede des Abgeordneten
     */
    @Override
    public String getAnrede() {
        return this.anrede;
    }

    /**
     * @return der akademische Titel des Abgeordneten
     */
    @Override
    public String getAkadTitel() {
        return this.akadTitel;
    }

    /**
     * @return das Geburtsdatum des Abgeordneten
     */
    @Override
    public Date getGeburtsDatum() {
        return this.geburtsdatum;
    }

    /**
     * @return der Geburtsort des Abgeordneten
     */
    @Override
    public String getGeburtsOrt() {
        return this.geburtsort;
    }

    /**
     * @return das Sterbedatum des Abgeordneten
     * @throws NullPointerException falls das Sterbedatum noch nicht vorliegt
     */
    @Override
    public Date getSterbeDatum() throws NullPointerException {
        if (this.sterbedatum == null) {
            throw new NullPointerException("Der Abgeordnete lebt noch und besitzt daher kein Sterbedatum");
        }
        return this.sterbedatum;
    }

    /**
     * @return das Geschlecht des Abgeordneten
     */
    @Override
    public Types.GESCHLECHT getGeschlecht() {
        return this.geschlecht;
    }

    /**
     * @return die Religion des Abgeordneten
     */
    @Override
    public String getReligion() {
        return this.religion;
    }

    /**
     * @return der Beruf des Abgeordneten
     */
    @Override
    public String getBeruf() {
        return this.beruf;
    }

    /**
     * @return eine kurze Lebensbeschreibung des Abgeordneten
     * @throws NullPointerException falls keine Vita vorliegt
     */
    @Override
    public String getVita() throws NullPointerException {
        if (this.vita.isEmpty()) {
            throw new NullPointerException("Es liegt keine VITA des Abgeordneten vor.");
        }
        return this.vita;
    }

    /**
     * @return Liste aller Mandate des Abgeordneten
     */
    @Override
    public Set<Mandat> listMandate() {
        return this.mandate;
    }

    /**
     * @param wahlperiode die Wahlperiode, nach der gefiltert werden soll
     * @return alle Mandate des Abgeordneten nach Wahlperiode
     */
    @Override
    public Set<Mandat> listMandate(Wahlperiode wahlperiode) {
        Set<Mandat> result = new HashSet<>();
        for (Mandat m : this.mandate) {
            if (m.getWahlperiode().getNumber() == wahlperiode.getNumber()) {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * @param wahlperiode die Wahlperiode, nach der gefiltert werden soll
     * @return Abfrage, ob Abgeordneter in angegebener Wahlperiode ein Mandat hatte
     */
    @Override
    public boolean hasMandat(Wahlperiode wahlperiode) {
        boolean hasMandat = false;

        for (Mandat m : this.mandate) {
            if (m.getWahlperiode().getNumber() == wahlperiode.getNumber()) {
                hasMandat = true;
                break;
            }
        }
        return hasMandat;
    }

    /**
     * @return Liste aller Mitgliedschaften des Abgeordneten
     */
    @Override
    public Set<Mitgliedschaft> listMitgliedschaften() {
        return this.mitgliedschaften;
    }

    /**
     * @param wahlperiode die Wahlperiode, nach der gefiltert werden soll
     * @return Liste aller Mitgliedschaften des Abgeordneten in einer Wahlperiode
     */
    @Override
    public Set<Mitgliedschaft> listMitgliedschaften(Wahlperiode wahlperiode) {
        return this.listMitgliedschaften().stream().filter(
                mitgliedschaft -> mitgliedschaft.getWahlperiode().getNumber() == wahlperiode.getNumber()).collect(Collectors.toSet());
    }

    /**
     * @return Liste aller Abstimmungen des Abgeordneten
     */
    @Override
    public Set<Abstimmung> listAbstimmungen() {
        return this.abstimmungen;
    }

    /**
     * @param wahlperiode die Wahlperiode, nach der gefiltert werden soll
     * @return Liste aller Abstimmungen des Abgeordneten für eine Wahlperiode
     */
    @Override
    public Set<Abstimmung> listAbstimmungen(Wahlperiode wahlperiode) {
        Set<Abstimmung> result = new HashSet<>();

        // Über das Datum der Abstimmung die Wahlperiode bestimmen und danach filtern:
        for (Abstimmung abstimmung : this.abstimmungen) {
            try {
                if (Objects.requireNonNull(StringFunctions.toDate(abstimmung.getBeschreibung().substring(0, 10))).after(wahlperiode.getStartDate())
                && wahlperiode.getEndDate() == null) {
                    result.add(abstimmung);
                } else if (Objects.requireNonNull(StringFunctions.toDate(abstimmung.getBeschreibung().substring(0, 10))).after(wahlperiode.getStartDate())
                    && Objects.requireNonNull(StringFunctions.toDate(abstimmung.getBeschreibung().substring(0, 10))).before(wahlperiode.getEndDate())) {
                    result.add(abstimmung);
                }
            } catch (BundestagException | ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * @param wahlperiode die Wahlperiode, nach der gefiltert werden soll
     * @param ergebnis das AErgebnis, nach dem gefiltert werden soll
     * @return Liste aller Abstimmungen des Abgeordneten für eine Wahlperiode und zu einem spezifischen Ergebnis
     */
    @Override
    public Set<Abstimmung> listAbstimmungen(Wahlperiode wahlperiode, Types.ABSTIMMUNG ergebnis) {
        return this.listAbstimmungen(wahlperiode).stream().filter(
                abstimmung -> abstimmung.getErgebnis().equals(ergebnis)).collect(Collectors.toSet());
    }

    /**
     * @return Gibt die Partei des Abgeordneten zurück
     * @throws BundestagException falls der Parteieintrag null ist
     */
    @Override
    public Partei getPartei() throws BundestagException {
        if (this.partei == null) {
            throw new AttributeNotFoundError("Das Parteiattribut des Abgeordneten " + this.name + " ist null");
        }
        return this.partei;
    }

    /**
     * Fügt ein Mandat zum Abgeordneten hinzu
     *
     * @param m das hinzuzufügende Mandat
     */
    public void addMandat(Mandat m){
        this.mandate.add(m);
    }

    /**
     * Fügt eine Mitgliedschaft zum Abgeordneten hinzu
     *
     * @param m die hinzuzufügende Mitgliedschaft
     */
    public void addMitgliedschaft(Mitgliedschaft m){
            this.mitgliedschaften.add(m);
    }

    /**
     * Fügt eine Abstimmung zum Abgeordneten hinzu
     *
     * @param a die hinzuzufügende Abstimmung
     */
    public void addAbstimmung(Abstimmung a){
        this.abstimmungen.add(a);
    }
}
