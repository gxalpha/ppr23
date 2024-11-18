package org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten;

/**
 * Biografische Angaben einer Person.<br>
 * Enthält Informationen wie Geburtsdatum und -ort, Geschlecht, Familienstand, Religion, etc.
 */
public interface BiografischeAngaben {
    public String getGeburtsdatum();

    public void setGeburtsdatum(String geburtsdatum);

    public String getGeburtsort();

    public void setGeburtsort(String geburtsort);

    public String getGeburtsland();

    public void setGeburtsland(String geburtsland);

    public String getSterbedatum();

    public void setSterbedatum(String sterbedatum);

    public String getGeschlecht();

    public void setGeschlecht(String geschlecht);

    public String getFamilienstand();

    public void setFamilienstand(String familienstand);

    public String getReligion();

    public void setReligion(String religion);

    public String getBeruf();

    public void setBeruf(String beruf);

    public String getParteiKurz();

    public void setParteiKurz(String parteiKurz);

    public String getVitaKurz();

    public void setVitaKurz(String vitaKurz);

    public String getVeroeffentlichungspflichtiges();

    public void setVeroeffentlichungspflichtiges(String veroeffentlichungspflichtiges);
}

class BiografischeAngabenImpl implements BiografischeAngaben {
    private String geburtsdatum;
    private String geburtsort;
    private String geburtsland;
    private String sterbedatum;
    private String geschlecht;
    private String familienstand;
    private String religion;
    private String beruf;
    private String parteiKurz;
    private String vitaKurz;
    private String veroeffentlichungspflichtiges;


    public BiografischeAngabenImpl(String geburtsdatum, String geburtsort, String geburtsland, String sterbedatum, String geschlecht, String familienstand, String religion, String beruf, String parteiKurz, String vitaKurz, String veroeffentlichungspflichtiges) {
        this.geburtsdatum = geburtsdatum;
        this.geburtsort = geburtsort;
        this.geburtsland = geburtsland;
        this.sterbedatum = sterbedatum;
        this.geschlecht = geschlecht;
        this.familienstand = familienstand;
        this.religion = religion;
        this.beruf = beruf;
        this.parteiKurz = parteiKurz;
        this.vitaKurz = vitaKurz;
        this.veroeffentlichungspflichtiges = veroeffentlichungspflichtiges;
    }

    @Override
    public String getGeburtsdatum() {
        return geburtsdatum;
    }

    @Override
    public void setGeburtsdatum(String geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    @Override
    public String getGeburtsort() {
        return geburtsort;
    }

    @Override
    public void setGeburtsort(String geburtsort) {
        this.geburtsort = geburtsort;
    }

    @Override
    public String getGeburtsland() {
        return geburtsland;
    }

    @Override
    public void setGeburtsland(String geburtsland) {
        this.geburtsland = geburtsland;
    }

    @Override
    public String getSterbedatum() {
        return sterbedatum;
    }

    @Override
    public void setSterbedatum(String sterbedatum) {
        this.sterbedatum = sterbedatum;
    }

    @Override
    public String getGeschlecht() {
        return geschlecht;
    }

    @Override
    public void setGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
    }

    @Override
    public String getFamilienstand() {
        return familienstand;
    }

    @Override
    public void setFamilienstand(String familienstand) {
        this.familienstand = familienstand;
    }

    @Override
    public String getReligion() {
        return religion;
    }

    @Override
    public void setReligion(String religion) {
        this.religion = religion;
    }

    @Override
    public String getBeruf() {
        return beruf;
    }

    @Override
    public void setBeruf(String beruf) {
        this.beruf = beruf;
    }

    @Override
    public String getParteiKurz() {
        return parteiKurz;
    }

    @Override
    public void setParteiKurz(String parteiKurz) {
        this.parteiKurz = parteiKurz;
    }

    @Override
    public String getVitaKurz() {
        return vitaKurz;
    }

    @Override
    public void setVitaKurz(String vitaKurz) {
        this.vitaKurz = vitaKurz;
    }

    @Override
    public String getVeroeffentlichungspflichtiges() {
        return veroeffentlichungspflichtiges;
    }

    @Override
    public void setVeroeffentlichungspflichtiges(String veroeffentlichungspflichtiges) {
        this.veroeffentlichungspflichtiges = veroeffentlichungspflichtiges;
    }
}
