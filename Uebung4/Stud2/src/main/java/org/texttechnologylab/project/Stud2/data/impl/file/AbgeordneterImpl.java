package org.texttechnologylab.project.Stud2.data.impl.file;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.data.Abgeordneter;
import org.texttechnologylab.project.Stud2.data.BundestagObject;
import org.texttechnologylab.project.Stud2.data.Mandat;
import org.texttechnologylab.project.Stud2.data.Mitgliedschaft;
import org.texttechnologylab.project.Stud2.data.Partei;
import org.texttechnologylab.project.Stud2.data.Rede;
import org.texttechnologylab.project.Stud2.data.Wahlperiode;
import org.texttechnologylab.project.Stud2.exceptions.AttributeNotFoundError;
import org.texttechnologylab.project.Stud2.exceptions.BundestagException;

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
public class AbgeordneterImpl extends BundestagObjectImpl implements Abgeordneter {
    protected final int ID;
    protected final String name;
    protected final String vorname;
    protected final String ortszusatz;
    protected final String adelssuffix;
    protected final String anrede;
    protected final String akadTitel;
    protected final Date geburtsdatum;
    protected final String geburtsort;
    protected final Date sterbedatum;
    protected final Types.GESCHLECHT geschlecht;
    protected final String religion;
    protected final String beruf;
    protected final String vita;
    protected List<Mandat> mandate;
    protected Set<Mitgliedschaft> mitgliedschaften;
    protected final Partei partei;
    protected List<Rede> reden;
    protected List<String> redenIDs = new ArrayList<>();

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
        this.mandate = new ArrayList<>();
        this.mitgliedschaften = new HashSet<>();
        this.partei = partei;
        this.reden = new ArrayList<>();
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
     * @return alle erfassten Reden des Abgeordneten
     */
    @Override
    public List<Rede> listReden() {
        return this.reden;
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
     * @return eine Liste aller IDs gehaltener Reden
     */
    public List<String> getRedenIDs() {
        return this.redenIDs;
    }

    /**
     * @return der Abgeordnete als Dokument
     */
    @Override
    public Document toDoc() throws BundestagException {
        Document doc = new Document();
        doc.append("_id", this.getID().toString());
        doc.append("partei", this.getPartei().getLabel());

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

        // Alle Wahlperioden (d.h. Mandate des Abgeordneten) speichern
        Document wahlperioden = new Document();
        for (Mandat mandat : this.listMandate()) {
            Document wahlperiode = new Document();
            wahlperiode.append("mdwp_von", mandat.fromDate());
            wahlperiode.append("mdwp_bis", mandat.toDate());
            wahlperiode.append("wahlkreis", mandat.getWahlkreis().getNumber());
            wahlperiode.append("liste", mandat.getListe());
            wahlperiode.append("mandatsart", (mandat.getTyp() == Types.MANDAT.DIREKTWAHL) ? "Direktwahl" : ((mandat.getTyp() == Types.MANDAT.LANDESLISTE) ? "Landesliste": "Volkskammer"));
            wahlperiode.append("fraktionen", mandat.getFraktionen().stream().map(BundestagObject::getLabel).collect(Collectors.toList()));

            // Alle Gruppenzugehörigkeiten dieser Wahlperiode speichern
            Set<String> g = new HashSet<>();
            for (Mitgliedschaft m : mandat.listMitgliedschaften()) {
                g.add(m.getGruppe().getLabel());
            }
            List<String> gruppen = new ArrayList<>(g);
            wahlperiode.append("gruppen", gruppen);

            wahlperioden.append("WP" + mandat.getWahlperiode().getNumber(), wahlperiode);

        }
        doc.append("wahlperioden", wahlperioden);

        // Alle Reden (IDs) des Abgeordneten speichern
        List<String> reden = new ArrayList<>();
        for (Rede rede : this.reden) {
            reden.add(rede.getID().toString());
        }
        doc.append("reden", reden);

        return doc;
    }
}
