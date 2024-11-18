package org.texttechnologylab.project.Stud1.data.impl;

import org.texttechnologylab.project.Stud1.data.Sitzung;
import org.texttechnologylab.project.Stud1.data.Tagesordnung;

import java.util.Set;
import java.util.UUID;

/**
 * Private Implementierung von Tagesordnung
 */
public class Tagesordnung_File_Impl implements Tagesordnung {
    private final String id;
    private final Sitzung sitzung;
    private final Set<String> tagesordnungspunkte;

    /**
     * @param sitzung             Sitzung
     * @param tagesordnungspunkte Menge aller Tagesordnungspunkte
     */
    public Tagesordnung_File_Impl(Sitzung sitzung, Set<String> tagesordnungspunkte) {
        this.id = UUID.randomUUID().toString();
        this.sitzung = sitzung;
        this.tagesordnungspunkte = tagesordnungspunkte;
    }

    /**
     * @return ID der Tagesordnung (und Sitzung)
     */
    @Override
    public String getID() {
        return id;
    }

    /**
     * @return Sitzung, dessen Tagesordnung das ist
     */
    @Override
    public Sitzung getSitzung() {
        return sitzung;
    }

    /**
     * @return Tagesordnungspunkte auf der Tagesordnung
     */
    @Override
    public Set<String> getTagesordnungspunkte() {
        return tagesordnungspunkte;
    }
}
