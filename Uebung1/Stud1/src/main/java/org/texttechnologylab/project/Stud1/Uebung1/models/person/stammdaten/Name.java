package org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten;

/**
 * Name einer Person.<br>
 * Enthält Vor- und Nachname, Titel, Adel, wowie die Zeit in der eine Person den Namen hat(te).
 */
public interface Name {
    public String getNachname();

    public void setNachname(String nachname);

    public String getVorname();

    public void setVorname(String vorname);

    public String getOrtszusatz();

    public void setOrtszusatz(String ortszusatz);

    public String getAdel();

    public void setAdel(String adel);

    public String getAnredeTitel();

    public void setAnredeTitel(String anredeTitel);

    public String getAkademischerTitel();

    public void setAkademischerTitel(String akademischerTitel);

    public String getHistorieVon();

    public void setHistorieVon(String historieVon);

    public String getHistorieBis();

    public void setHistorieBis(String historieBis);
}

class NameImpl implements Name {
    private String nachname;
    private String vorname;
    private String ortszusatz;
    private String adel;
    private String anredeTitel;
    private String akademischerTitel;
    private String historieVon;
    private String historieBis;

    public NameImpl(String nachname, String vorname, String ortszusatz, String adel, String anredeTitel, String akademischerTitel, String historieVon, String historieBis) {
        this.nachname = nachname;
        this.vorname = vorname;
        this.ortszusatz = ortszusatz;
        this.adel = adel;
        this.anredeTitel = anredeTitel;
        this.akademischerTitel = akademischerTitel;
        this.historieVon = historieVon;
        this.historieBis = historieBis;
    }

    @Override
    public String getNachname() {
        return nachname;
    }

    @Override
    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    @Override
    public String getVorname() {
        return vorname;
    }

    @Override
    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    @Override
    public String getOrtszusatz() {
        return ortszusatz;
    }

    @Override
    public void setOrtszusatz(String ortszusatz) {
        this.ortszusatz = ortszusatz;
    }

    @Override
    public String getAdel() {
        return adel;
    }

    @Override
    public void setAdel(String adel) {
        this.adel = adel;
    }

    @Override
    public String getAnredeTitel() {
        return anredeTitel;
    }

    @Override
    public void setAnredeTitel(String anredeTitel) {
        this.anredeTitel = anredeTitel;
    }

    @Override
    public String getAkademischerTitel() {
        return akademischerTitel;
    }

    @Override
    public void setAkademischerTitel(String akademischerTitel) {
        this.akademischerTitel = akademischerTitel;
    }

    @Override
    public String getHistorieVon() {
        return historieVon;
    }

    @Override
    public void setHistorieVon(String historieVon) {
        this.historieVon = historieVon;
    }

    @Override
    public String getHistorieBis() {
        return historieBis;
    }

    @Override
    public void setHistorieBis(String historieBis) {
        this.historieBis = historieBis;
    }
}
