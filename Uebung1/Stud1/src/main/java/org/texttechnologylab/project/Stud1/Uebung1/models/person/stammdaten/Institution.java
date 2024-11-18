package org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten;

/**
 * Eine Institution, in der ein Abgeordneter ist.<br>
 * Enthält die Art der Institution, den Namen, und von wann der Abgeordnete ein Teil der Institution ist.
 */
public interface Institution {
    public String getInsartLang();

    public void setInsartLang(String insartLang);

    public String getInsLang();

    public void setInsLang(String insLang);

    public String getMdbinsVon();

    public void setMdbinsVon(String mdbinsVon);

    public String getMdbinsBis();

    public void setMdbinsBis(String mdbinsBis);

    public String getFktinsVon();

    public void setFktinsVon(String fktinsVon);

    public String getFktinsBis();

    public void setFktinsBis(String fktinsBis);
}

class InstitutionImpl implements Institution {
    private String insartLang;
    private String insLang;
    private String mdbinsVon;
    private String mdbinsBis;
    private String fktinsVon;
    private String fktinsBis;

    public InstitutionImpl(String insartLang, String insLang, String mdbinsVon, String mdbinsBis, String fktinsVon, String fktinsBis) {
        this.insartLang = insartLang;
        this.insLang = insLang;
        this.mdbinsVon = mdbinsVon;
        this.mdbinsBis = mdbinsBis;
        this.fktinsVon = fktinsVon;
        this.fktinsBis = fktinsBis;
    }

    @Override
    public String getInsartLang() {
        return insartLang;
    }

    @Override
    public void setInsartLang(String insartLang) {
        this.insartLang = insartLang;
    }

    @Override
    public String getInsLang() {
        return insLang;
    }

    @Override
    public void setInsLang(String insLang) {
        this.insLang = insLang;
    }

    @Override
    public String getMdbinsVon() {
        return mdbinsVon;
    }

    @Override
    public void setMdbinsVon(String mdbinsVon) {
        this.mdbinsVon = mdbinsVon;
    }

    @Override
    public String getMdbinsBis() {
        return mdbinsBis;
    }

    @Override
    public void setMdbinsBis(String mdbinsBis) {
        this.mdbinsBis = mdbinsBis;
    }

    @Override
    public String getFktinsVon() {
        return fktinsVon;
    }

    @Override
    public void setFktinsVon(String fktinsVon) {
        this.fktinsVon = fktinsVon;
    }

    @Override
    public String getFktinsBis() {
        return fktinsBis;
    }

    @Override
    public void setFktinsBis(String fktinsBis) {
        this.fktinsBis = fktinsBis;
    }
}
