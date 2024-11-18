package org.texttechnologylab.project.Stud2.impl;

import org.texttechnologylab.project.Stud2.personen.MitarbeiterIF;

// objects of this class work for objects of the class 'Abgeordneter'
public class Mitarbeiter implements MitarbeiterIF {
    private int mitarbeiterID;
    private Abgeordneter vorgesetzter;
    private int beschaeftigungsumfang;
    private Raumbelegung[] termine;

    // Getter and Setter
    public int getMitarbeiterID() {
        return mitarbeiterID;
    }

    public void setMitarbeiterID(int mitarbeiterID) {
        this.mitarbeiterID = mitarbeiterID;
    }

    public Abgeordneter getVorgesetzter() {
        return vorgesetzter;
    }

    public void setVorgesetzter(Abgeordneter vorgesetzter) {
        this.vorgesetzter = vorgesetzter;
    }

    public int getBeschaeftigungsumfang() {
        return beschaeftigungsumfang;
    }

    public void setBeschaeftigungsumfang(int beschaeftigungsumfang) {
        this.beschaeftigungsumfang = beschaeftigungsumfang;
    }

    public Raumbelegung[] getTermine() {
        return termine;
    }

    public void setTermine(Raumbelegung[] termine) {
        this.termine = termine;
    }
}
