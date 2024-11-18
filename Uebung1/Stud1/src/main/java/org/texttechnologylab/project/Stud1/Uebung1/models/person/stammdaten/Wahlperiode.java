package org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten;

/**
 * Eine Wahlperiode eines Abgeordneten.<br>
 * Enthält Informationen über die Wahlperiode, wie die Zeit, den Wahlkreis, die Art des Mandats, etc.
 */
public interface Wahlperiode {
    public int getWp();

    public void setWp(int wp);

    public String getMdbwpVon();

    public void setMdbwpVon(String mdbwpVon);

    public String getMdbwpBis();

    public void setMdbwpBis(String mdbwpBis);

    public int getWpNummer();

    public void setWpNummer(int wpNummer);

    public String getWkrName();

    public void setWkrName(String wkrName);

    public String getWkrLand();

    public void setWkrLand(String wkrLand);

    public String getListe();

    public void setListe(String liste);

    public String getMandatsart();

    public void setMandatsart(String mandatsart);

    public Institution[] getInstitutionen();

    public void setInstitutionen(Institution[] institutionen);
}

class WahlperiodeImpl implements Wahlperiode {
    private int wp;
    private String mdbwpVon;
    private String mdbwpBis;
    private int wpNummer;
    private String wkrName;
    private String wkrLand;
    private String liste;
    private String mandatsart;
    private Institution[] institutionen;

    public WahlperiodeImpl(int wp, String mdbwpVon, String mdbwpBis, int wpNummer, String wkrName, String wkrLand, String liste, String mandatsart, Institution[] institutionen) {
        this.wp = wp;
        this.mdbwpVon = mdbwpVon;
        this.mdbwpBis = mdbwpBis;
        this.wpNummer = wpNummer;
        this.wkrName = wkrName;
        this.wkrLand = wkrLand;
        this.liste = liste;
        this.mandatsart = mandatsart;
        this.institutionen = institutionen;
    }

    @Override
    public int getWp() {
        return wp;
    }

    @Override
    public void setWp(int wp) {
        this.wp = wp;
    }

    @Override
    public String getMdbwpVon() {
        return mdbwpVon;
    }

    @Override
    public void setMdbwpVon(String mdbwpVon) {
        this.mdbwpVon = mdbwpVon;
    }

    @Override
    public String getMdbwpBis() {
        return mdbwpBis;
    }

    @Override
    public void setMdbwpBis(String mdbwpBis) {
        this.mdbwpBis = mdbwpBis;
    }

    @Override
    public int getWpNummer() {
        return wpNummer;
    }

    @Override
    public void setWpNummer(int wpNummer) {
        this.wpNummer = wpNummer;
    }

    @Override
    public String getWkrName() {
        return wkrName;
    }

    @Override
    public void setWkrName(String wkrName) {
        this.wkrName = wkrName;
    }

    @Override
    public String getWkrLand() {
        return wkrLand;
    }

    @Override
    public void setWkrLand(String wkrLand) {
        this.wkrLand = wkrLand;
    }

    @Override
    public String getListe() {
        return liste;
    }

    @Override
    public void setListe(String liste) {
        this.liste = liste;
    }

    @Override
    public String getMandatsart() {
        return mandatsart;
    }

    @Override
    public void setMandatsart(String mandatsart) {
        this.mandatsart = mandatsart;
    }

    @Override
    public Institution[] getInstitutionen() {
        return institutionen;
    }

    @Override
    public void setInstitutionen(Institution[] institutionen) {
        this.institutionen = institutionen;
    }
}
