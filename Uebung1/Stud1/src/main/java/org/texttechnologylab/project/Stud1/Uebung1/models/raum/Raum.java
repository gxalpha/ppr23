package org.texttechnologylab.project.Stud1.Uebung1.models.raum;

/**
 * Ein Raum.<br>
 * Enthält Bezeichung (Raumnummer/-name) und Kapazität.
 */
public interface Raum {
    public String getRaumbezeichung();

    public void setRaumbezeichung(String raumbezeichung);

    public int getKapazitaet();

    public void setKapazitaet(int kapazitaet);
}

class RaumImpl implements Raum {
    private String raumbezeichung;
    private int kapazitaet;

    public RaumImpl(String raumbezeichung, int kapazitaet) {
        this.raumbezeichung = raumbezeichung;
        this.kapazitaet = kapazitaet;
    }

    @Override
    public String getRaumbezeichung() {
        return raumbezeichung;
    }

    @Override
    public void setRaumbezeichung(String raumbezeichung) {
        this.raumbezeichung = raumbezeichung;
    }

    @Override
    public int getKapazitaet() {
        return kapazitaet;
    }

    @Override
    public void setKapazitaet(int kapazitaet) {
        this.kapazitaet = kapazitaet;
    }
}
