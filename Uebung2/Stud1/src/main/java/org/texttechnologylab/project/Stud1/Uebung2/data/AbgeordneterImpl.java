package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.*;
import org.texttechnologylab.project.exception.BundestagException;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Private Hilfsklasse zum Speichern von Namensinformationen
 */
class AbgeordneterName {
    private final String vorname;
    private final String nachname;
    private final String ortszusatz;
    private final String adelssuffix;
    private final String anrede;
    private final String akadTitel;
    private final Date von;
    private final Date bis;

    /**
     * @param vorname     Vorname
     * @param nachname    Nachname
     * @param ortszusatz  Ortszusatz
     * @param adelssuffix Adelssuffix
     * @param anrede      Anrede des Namen (Dr. etc)
     * @param akadTitel   Akademischer Titel
     * @param von         Von wann der Name gültig ist
     * @param bis         Bis wann er Name gültig ist (darf null sein)
     */
    public AbgeordneterName(String vorname, String nachname, String ortszusatz, String adelssuffix, String anrede, String akadTitel, Date von, Date bis) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.ortszusatz = ortszusatz;
        this.adelssuffix = adelssuffix;
        this.anrede = anrede;
        this.akadTitel = akadTitel;
        this.von = von;
        this.bis = bis;
    }

    /**
     * @return Der Vorname
     */
    public String getVorname() {
        return vorname;
    }

    /**
     * @return Der Nachname. In anderen Interfaces auch nur "name" genannt
     */
    public String getNachname() {
        return nachname;
    }

    /**
     * @return Ortszusatz des Namen
     */
    public String getOrtszusatz() {
        return ortszusatz;
    }

    /**
     * @return Adelssuffix des Namen
     */
    public String getAdelssuffix() {
        return adelssuffix;
    }

    /**
     * @return Anrede des Namen
     */
    public String getAnrede() {
        return anrede;
    }

    /**
     * @return Akademischer Titel des Namen
     */
    public String getAkadTitel() {
        return akadTitel;
    }

    /**
     * @return Von wann der Name gültig ist
     */
    public Date getVon() {
        return von;
    }

    /**
     * @return Bis wann der Name gültig ist. Kann null sein.
     */
    public Date getBis() {
        return bis;
    }
}

/**
 * Private Implementierung von Abgeordneter
 */
class AbgeordneterImpl extends BundestagObjectImpl implements Abgeordneter {
    private final Integer id;
    private final Date geburtsdatum;
    private final String geburtsort;
    private final Date sterbedatum;
    private final Types.GESCHLECHT geschlecht;
    private final String religion;
    private final String beruf;
    private final String vita;
    private final Set<Mandat> mandate;
    private final Set<Mitgliedschaft> mitgliedschaften;
    private final Set<ZeitlicheAbstimmung> abstimmungen;
    private final Partei partei;
    private final List<AbgeordneterName> namen;

    /**
     * @param id           ID des Abgeordneten
     * @param namen        Liste aller Namen
     * @param geburtsdatum Geburtsdatum
     * @param geburtsort   Geburtsort
     * @param sterbedatum  Sterbedatum falls anwendbar, sonst null
     * @param geschlecht   Geschlecht
     * @param religion     Religion
     * @param beruf        Beruf
     * @param vita         Kurzvita falls vorhanden, sonst null
     * @param partei       Partei
     */
    AbgeordneterImpl(Integer id, List<AbgeordneterName> namen, Date geburtsdatum, String geburtsort, Date sterbedatum, Types.GESCHLECHT geschlecht, String religion, String beruf, String vita, Partei partei) throws BundestagException {
        super();

        if (namen.isEmpty()) {
            throw new BundestagException("Abgeordneter hat keinen Namen");
        }

        this.id = id;
        this.namen = namen;
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
     * @return Alle Namen des Abgeordneten
     */
    List<AbgeordneterName> getNamen() {
        return namen;
    }

    /**
     * @return Primaerer Name des Abgeordneten
     */
    private AbgeordneterName getPrimaererName() {
        // Vermutlich ist es der letzte Name
        if (namen.get(namen.size() - 1).getBis() == null)
            return namen.get(namen.size() - 1);

        // Wenn nicht, versuche alle.
        for (AbgeordneterName name : getNamen()) {
            if (name.getBis() == null)
                return name;
        }
        throw new RuntimeException("Abgeordneter hat keinen aktuellen Namen");
    }

    /**
     * Fügt einem Abgeordneten ein Mandat hinzu
     *
     * @param mandat Das Mandat
     */
    void addMandat(Mandat mandat) {
        mandate.add(mandat);
    }

    /**
     * Fügt einem Abgeordneten eine Mitgliedschaft hinzu
     *
     * @param mitgliedschaft Die Mitgliedschaft
     */
    void addMitgliedschaft(Mitgliedschaft mitgliedschaft) {
        mitgliedschaften.add(mitgliedschaft);
    }

    /**
     * Fügt einem Abgeordneten eine Abstimmung hinzu
     *
     * @param abstimmung Abstimmung mit Zeitinformation
     */
    void addAbstimmung(ZeitlicheAbstimmung abstimmung) {
        abstimmungen.add(abstimmung);
    }

    /**
     * @return Name des Abgeordneten
     */
    @Override
    public String getName() {
        return getPrimaererName().getNachname();
    }

    /**
     * @return Vorname des Abgeordneten
     */
    @Override
    public String getVorname() {
        return getPrimaererName().getVorname();
    }

    /**
     * @return Ortszusatz der Adresse des Abgeordneten
     */
    @Override
    public String getOrtszusatz() {
        return getPrimaererName().getOrtszusatz();
    }

    /**
     * @return Adelssuffix des Abgeordneten
     */
    @Override
    public String getAdelssuffix() {
        return getPrimaererName().getAdelssuffix();
    }

    /**
     * @return Anrede des Abgeordneten
     */
    @Override
    public String getAnrede() {
        return getPrimaererName().getAnrede();
    }

    /**
     * @return Akademischer Titel des Abgeordneten
     */
    @Override
    public String getAkadTitel() {
        return getPrimaererName().getAkadTitel();
    }

    /**
     * @return Geburtsdatum des Abgeordneten
     */
    @Override
    public Date getGeburtsDatum() {
        return geburtsdatum;
    }

    /**
     * @return Geburtsort des Abgeordneten
     */
    @Override
    public String getGeburtsOrt() {
        return geburtsort;
    }

    /**
     * @return Sterbedatum des Abgeordneten
     * @throws NullPointerException Wenn der Abgeordnete noch lebt
     */
    @Override
    public Date getSterbeDatum() throws NullPointerException {
        if (sterbedatum == null) {
            throw new NullPointerException("Person hasn't died yet");
        } else {
            return sterbedatum;
        }
    }

    /**
     * @return Geschlecht des Abgeordneten
     */
    @Override
    public Types.GESCHLECHT getGeschlecht() {
        return geschlecht;
    }

    /**
     * @return Religionszugehörigheit des Abgeordneten
     */
    @Override
    public String getReligion() {
        return religion;
    }

    /**
     * @return Beruf des Abgeordneten
     */
    @Override
    public String getBeruf() {
        return beruf;
    }

    /**
     * @return Vita des Abgeordneten
     * @throws NullPointerException wenn keine Vita verfügbar ist
     */
    @Override
    public String getVita() throws NullPointerException {
        if (vita == null) {
            throw new NullPointerException("No vita available");
        } else {
            return vita;
        }
    }

    /**
     * @return Menge der Mandate des Abgeordneten
     */
    @Override
    public Set<Mandat> listMandate() {
        return mandate;
    }

    /**
     * @param wahlperiode Wahlperiode
     * @return Menge der Mandate des Abgeordneten in der gegebenen Wahlperiode
     */
    @Override
    public Set<Mandat> listMandate(Wahlperiode wahlperiode) {
        Set<Mandat> passendeMandate = new HashSet<>();
        for (Mandat mandat : listMandate()) {
            if (wahlperiode.equals(mandat.getWahlperiode())) {
                passendeMandate.add(mandat);
            }
        }
        return passendeMandate;
    }

    /**
     * @param wahlperiode Wahlperiode
     * @return Ob der Abgeordnete in der gegebenen Wahlperiode ein Mandat hat
     */
    @Override
    public boolean hasMandat(Wahlperiode wahlperiode) {
        Set<Mandat> mandate = listMandate(wahlperiode);
        return !mandate.isEmpty();
    }

    /**
     * @return Die Menge aller Mitgliedschaften des Abgeordneten
     */
    @Override
    public Set<Mitgliedschaft> listMitgliedschaften() {
        return mitgliedschaften;
    }

    /**
     * @param wahlperiode Wahlperiode
     * @return Die Menge aller Mitgliedschaften des Abgeordneten in der gegebenen Wahlperiode
     */
    @Override
    public Set<Mitgliedschaft> listMitgliedschaften(Wahlperiode wahlperiode) {
        Set<Mitgliedschaft> passendeMitgliedschaften = new HashSet<>();
        for (Mitgliedschaft mitgliedschaft : listMitgliedschaften()) {
            if (wahlperiode.equals(mitgliedschaft.getWahlperiode())) {
                passendeMitgliedschaften.add(mitgliedschaft);
            }
        }
        return passendeMitgliedschaften;
    }

    /**
     * Lokale Hilfsfunktion
     *
     * @return Menge aller Abstimmungen (mit Zeit), an denen der Abgeordnete teilgenommen hat
     */
    private Set<ZeitlicheAbstimmung> listZeitlicheAbstimmungen() {
        return abstimmungen;
    }

    /**
     * @return Menge aller Abstimmungen, an denen der Abgeordnete teilgenommen hat
     */
    @Override
    public Set<Abstimmung> listAbstimmungen() {
        // Leider können wir hier nicht einfach `abstimmungen` zurückgeben
        return new HashSet<>(listZeitlicheAbstimmungen());
    }

    /**
     * @param wahlperiode Wahlperiode
     * @return Menge aller Abstimmungen, an denen der Abgeordnete in der gegebenen Wahlperiode teilgenommen hat
     */
    @Override
    public Set<Abstimmung> listAbstimmungen(Wahlperiode wahlperiode) {
        Set<Abstimmung> passendeAbstimmungen = new HashSet<>();
        for (ZeitlicheAbstimmung abstimmung : listZeitlicheAbstimmungen()) {
            if (wahlperiode.equals(abstimmung.getWahlperiode())) {
                passendeAbstimmungen.add(abstimmung);
            }
        }
        return passendeAbstimmungen;
    }

    /**
     * @param wahlperiode    Wahlperiode
     * @param abstimmungsart Art der Abstimmung
     * @return Menge aller Abstimmungen, an denen der Abgeordnete in der gegebenen  teilgenommen hat
     */
    @Override
    public Set<Abstimmung> listAbstimmungen(Wahlperiode wahlperiode, Types.ABSTIMMUNG abstimmungsart) {
        Set<Abstimmung> passendeAbstimmungen = new HashSet<>();
        for (Abstimmung abstimmung : listAbstimmungen(wahlperiode)) {
            if (abstimmung.getErgebnis() == abstimmungsart) {
                passendeAbstimmungen.add(abstimmung);
            }
        }
        return passendeAbstimmungen;
    }

    /**
     * @return Partei des Abgeordneten
     * @throws BundestagException Falls der Abgeordnete keiner Partei angehört
     */
    @Override
    public Partei getPartei() throws BundestagException {
        if (partei == null) {
            throw new BundestagException("Abgeordneter gehört keiner Partei an");
        } else {
            return partei;
        }
    }

    /**
     * @return ID des Abgeordneten
     */
    @Override
    public Object getID() {
        return id;
    }

    /**
     * @return Label des Abgeordneten
     */
    @Override
    public String getLabel() {
        String name = "";
        if (!getAnrede().isEmpty()) {
            name += getAnrede();
            name += " ";
        }
        name += getVorname();
        name += " ";
        name += getName();
        name += ", " + partei.getLabel();
        return name;
    }
}
