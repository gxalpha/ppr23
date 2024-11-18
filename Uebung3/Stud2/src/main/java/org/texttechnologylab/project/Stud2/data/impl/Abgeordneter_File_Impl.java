package org.texttechnologylab.project.Stud2.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.data.Abgeordneter;
import org.texttechnologylab.project.Stud2.data.Kommentar;
import org.texttechnologylab.project.Stud2.data.Mandat;
import org.texttechnologylab.project.Stud2.data.Mitgliedschaft;
import org.texttechnologylab.project.Stud2.data.Partei;
import org.texttechnologylab.project.Stud2.data.Rede;
import org.texttechnologylab.project.Stud2.data.Types;
import org.texttechnologylab.project.Stud2.data.Wahlperiode;
import org.texttechnologylab.project.Stud2.exception.AttributeNotFoundError;
import org.texttechnologylab.project.Stud2.exception.BundestagException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Klasse für einen Abgeordneten
 *
 * @author Stud2
 */
public class Abgeordneter_File_Impl extends BundestagObject_Impl implements Abgeordneter {
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
    private List<Mandat> mandate;
    private Set<Mitgliedschaft> mitgliedschaften;
    private final Partei partei;
    private final List<Rede> reden;
    private List<Kommentar> kommentare;

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
    public Abgeordneter_File_Impl(int ID, String name, String vorname, String ortszusatz, String adelssuffix, String anrede,
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
        this.mandate = new ArrayList<>();
        this.mitgliedschaften = new HashSet<>();
        this.partei = partei;
        this.reden = new ArrayList<>();
        this.kommentare = new ArrayList<>();
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
     */
    @Override
    public Date getSterbeDatum() {
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
     */
    @Override
    public String getVita() {
        return this.vita;
    }

    /**
     * @return Liste aller Mandate des Abgeordneten
     */
    @Override
    public List<Mandat> listMandate() {
        return this.mandate;
    }

    /**
     * @param wahlperiode die Wahlperiode, nach der gefiltert werden soll
     * @return alle Mandate des Abgeordneten nach Wahlperiode
     */
    @Override
    public List<Mandat> listMandate(Wahlperiode wahlperiode) {
        List<Mandat> result = new ArrayList<>();
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
    @Override
    public void addMandat(Mandat m){
        this.mandate.add(m);
    }

    /**
     * Fügt eine Mitgliedschaft zum Abgeordneten hinzu
     *
     * @param m die hinzuzufügende Mitgliedschaft
     */
    @Override
    public void addMitgliedschaft(Mitgliedschaft m){
            this.mitgliedschaften.add(m);
    }

    /**
     * Fügt eine Rede zum Abgeordneten hinzu
     *
     * @param rede die hinzuzufügende Rede
     */
    @Override
    public void addRede(Rede rede) {
        this.reden.add(rede);
    }

    /**
     * Fügt einen Kommentar zum Abgeordneten hinzu
     *
     * @param k der Kommentar, der zum Abgeordneten zugeordnet werden soll
     */
    @Override
    public void addKommentar(Kommentar k) {
        this.kommentare.add(k);
    }

    /**
     * @return der Abgeordnete als Dokument
     */
    @Override
    public Document toDoc() throws BundestagException {
        Document doc = new Document();
        doc.append("_id", this.getID().toString());
        doc.append("partei", this.getPartei().getLabel());
        doc.append("fraktion", this.listMandate().stream().findAny().get().getFraktionen().stream().findAny().get().getLabel());

        // Details zum Namen speichern
        Document name = new Document();
        name.append("nachname", this.getName());
        name.append("vorname", this.getVorname());
        name.append("ortszusatz", this.getOrtszusatz());
        name.append("adel", this.getAdelssuffix());
        name.append("anrede", this.getAnrede());
        name.append("titel", this.getAkadTitel());
        doc.append("name", name);

        // Biografische Angaben speichern
        Document biografie = new Document();
        biografie.append("geburtsdatum", this.getGeburtsDatum());
        biografie.append("geburtsort", this.getGeburtsOrt());
        biografie.append("sterbedatum", this.getSterbeDatum());
        biografie.append("geschlecht", (this.getGeschlecht() == Types.GESCHLECHT.MAENNLICH) ? "m" : "w");
        biografie.append("religion", this.getReligion());
        biografie.append("beruf", this.getBeruf());
        biografie.append("vita_kurz", this.getVita());
        doc.append("biografie", biografie);

        // Alle Wahlperioden (d.h. Mandate des Abgeordneten) einspeichern
        Document wahlperioden = new Document();
        for (Mandat mandat : this.listMandate()) {
            Document wahlperiode = new Document();
            wahlperiode.append("mdwp_von", mandat.fromDate());
            wahlperiode.append("mdwp_bis", mandat.toDate());
            wahlperiode.append("wahlkreis", mandat.getWahlkreis().getNumber());
            wahlperiode.append("liste", mandat.getListe());
            wahlperiode.append("mandatsart", (mandat.getTyp() == Types.MANDAT.DIREKTWAHL) ? "Direktwahl" : "Listenwahl");
            wahlperiode.append("fraktion", mandat.getFraktionen().stream().findAny().get().getLabel());
            wahlperioden.append("WP" + mandat.getWahlperiode().getNumber(), wahlperiode);
        }
        doc.append("wahlperioden", wahlperioden);

        // Alle Kommentare des Abgeordneten einspeichern
        List<String> kommentare = new ArrayList<>();
        for (Kommentar kommentar : this.kommentare) {
            kommentare.add((String) kommentar.getID());
        }
        doc.append("kommentare", kommentare);

        // Alle Reden des Abgeordneten einspeichern
        List<String> reden = new ArrayList<>();
        for (Rede rede : this.reden) {
            reden.add(rede.getID().toString());
        }
        doc.append("reden", reden);

        return doc;
    }
}
