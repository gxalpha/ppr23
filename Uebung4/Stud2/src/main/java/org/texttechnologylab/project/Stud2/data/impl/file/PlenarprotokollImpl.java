package org.texttechnologylab.project.Stud2.data.impl.file;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.data.Rede;
import org.texttechnologylab.project.Stud2.data.Plenarprotokoll;
import org.texttechnologylab.project.Stud2.data.Wahlperiode;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Eine Klasse für eine Sitzung
 *
 * @author Stud2
 */
public class PlenarprotokollImpl extends BundestagObjectImpl implements Plenarprotokoll {

    private final Wahlperiode wahlperiode;
    private final String ort;
    private final int nummer;
    private final Date datum;
    private final Date beginn;
    private final Date ende;
    private final List<Rede> reden;

    /**
     * Der Konstruktor für eine Sitzung
     *
     * @param label das Label der Sitzung
     * @param wahlperiode die Wahlperiode, in der die Sitzung gehalten wurde
     * @param ort der Ort der Sitzung
     * @param nummer die Sitzungsnummer
     * @param datum das Datum der Sitzung
     * @param beginn das Datum inklusive der Startzeit der Sitzung
     * @param ende das Datum inklusive der Endzeit der Sitzung
     * @param reden die Reden der Abgeordneten dieser Sitzung
     */
    public PlenarprotokollImpl(String label, Wahlperiode wahlperiode, String ort, int nummer,
                               Date datum, Date beginn, Date ende, List<Rede> reden) {
        super(label);
        this.wahlperiode = wahlperiode;
        this.ort = ort;
        this.nummer = nummer;
        this.datum = datum;
        this.beginn = beginn;
        this.ende = ende;
        this.reden = reden;
    }

    /**
     * @return die ID der Sitzung (Wahlperiode * 1000 + Sitzungsnummer)
     */
    @Override
    public Object getID() {
        return this.getWahlperiode().getNumber() * 1000 + this.getNumber();
    }

    /**
     * @return die Wahlperiode der Sitzung
     */
    @Override
    public Wahlperiode getWahlperiode() {
        return this.wahlperiode;
    }

    /**
     * @return der Ort der Sitzung
     */
    @Override
    public String getOrt() {
        return this.ort;
    }

    /**
     * @return die Nummer der Sitzung
     */
    @Override
    public int getNumber() {
        return this.nummer;
    }

    /**
     * @return das Datum der Sitzung
     */
    @Override
    public Date getDate() {
        return this.datum;
    }

    /**
     * @return der Beginn der Sitzung der Form dd.MM.yyyy HH:mm
     */
    @Override
    public Date getBeginn() {
        return this.beginn;
    }

    /**
     * @return das Ende der Sitzung der Form dd.MM.yyyy HH:mm
     */
    @Override
    public Date getEnde() {
        return this.ende;
    }

    /**
     * @return alle Reden dieser Sitzung
     */
    @Override
    public List<Rede> getReden() {
        return this.reden;
    }

    /**
     * @return die Sitzung als Dokument
     */
    @Override
    public Document toDoc() throws Exception {
        Document document = new Document();
        document.append("_id", this.getID().toString());
        document.append("wahlperiode", this.getWahlperiode().getNumber());
        document.append("sitzungsnummer", this.getNumber());
        document.append("ort", this.getOrt());
        document.append("datum", this.getDate());
        document.append("beginn", this.getBeginn());
        document.append("ende", this.getEnde());

        List<Document> reden = new ArrayList<>();
        for (Rede r : this.getReden()) {
            reden.add(r.toDoc());
        }

        document.append("reden", reden);

        return document;
    }
}
