package org.texttechnologylab.project.Stud1.data.impl;

import org.texttechnologylab.project.Stud1.data.Rede;
import org.texttechnologylab.project.Stud1.data.Sitzung;
import org.texttechnologylab.project.Stud1.data.Tagesordnung;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Private Implementierung von Sitzung
 */
public class Sitzung_File_Impl implements Sitzung {
    private final Date date;
    private final Set<Rede> reden;
    private int dauer;
    private Tagesordnung tagesordnung;

    /**
     * @param date Datum der Sitzung
     */
    public Sitzung_File_Impl(Date date) {
        this.date = date;
        this.dauer = -1;
        this.reden = new HashSet<>();
    }

    /**
     * @return ID der Sitzung. Unique.
     */
    @Override
    public String getID() {
        return getTagesordnung().getID();
    }

    /**
     * @return Datum der Sitzung
     */
    @Override
    public Date getDate() {
        return date;
    }

    /**
     * @return Dauer der Sitzung
     */
    @Override
    public int getDauer() {
        return dauer;
    }

    /**
     * @return Tagesordnung der Sitzung
     */
    @Override
    public Tagesordnung getTagesordnung() {
        return tagesordnung;
    }

    /**
     * Setzt die Tagesordnung der Liste.
     * Nur erlaubt, wenn die Sitzung keine Tagesordnung hat.
     *
     * @param tagesordnung Die Tagesordnung
     * @throws RuntimeException Wenn die Sitzung schon eine Tagesordnung hat
     */
    void setTagesordnung(Tagesordnung tagesordnung) {
        if (this.tagesordnung == null) {
            this.tagesordnung = tagesordnung;
        } else {
            throw new RuntimeException("Sitzung hat schon eine Tagesordnung");
        }
    }

    /**
     * Fügt der Sitzung eine Rede hinzu
     *
     * @param rede Die Rede
     */
    void addRede(Rede rede) {
        reden.add(rede);
    }

    /**
     * Setzt die Dauer der Liste.
     * Nur erlaubt, wenn die Sitzung keine Dauer hat.
     *
     * @param dauer Dauer der Sitzung
     * @throws RuntimeException Wenn die Sitzung schon eine Dauer hat
     */
    void setDauer(int dauer) {
        if (this.dauer == -1) {
            this.dauer = dauer;
        } else {
            throw new RuntimeException("Sitzung hat schon eine Dauer");
        }
    }

    /**
     * @return Reden, die während einem der TOP gehalten wurden
     */
    @Override
    public Set<Rede> getReden() {
        return new HashSet<>(reden);
    }
}
