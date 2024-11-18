package org.texttechnologylab.project.Stud2.impl;

import org.texttechnologylab.project.Stud2.personen.AbgeordneterIF;

import java.util.Date;

public class Abgeordneter implements AbgeordneterIF {
    private int AbgeordnetenID;
    private int budget;
    private Mitarbeiter[] angestellte;
    private Raumbelegung[ ] termine;
    private Fraktion fraktion;
    private String partei_kurz;
    private String vita_kurz;
    private String veroeffentlichungspflichtiges;
    private int wahlperiode;
    private Date mdbwp_von;
    private Date mdbwp_bis;
    private int wkr_nummer;
    private String wkr_name;
    private String wkr_land;
    private String liste;
    private String mandatsart;
    private String[] institutionen;

    public int getAbgeordnetenID() {
        return AbgeordnetenID;
    }

    public void setAbgeordnetenID(int abgeordnetenID) {
        AbgeordnetenID = abgeordnetenID;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public Mitarbeiter[] getAngestellte() {
        return angestellte;
    }

    public void setAngestellte(Mitarbeiter[] angestellte) {
        this.angestellte = angestellte;
    }

    public Raumbelegung[] getTermine() {
        return termine;
    }

    public void setTermine(Raumbelegung[] termine) {
        this.termine = termine;
    }

    public Fraktion getFraktion() {
        return fraktion;
    }

    public void setFraktion(Fraktion fraktion) {
        this.fraktion = fraktion;
    }

    public String getPartei_kurz() {
        return partei_kurz;
    }

    public void setPartei_kurz(String partei_kurz) {
        this.partei_kurz = partei_kurz;
    }

    public String getVita_kurz() {
        return vita_kurz;
    }

    public void setVita_kurz(String vita_kurz) {
        this.vita_kurz = vita_kurz;
    }

    public String getVeroeffentlichungspflichtiges() {
        return veroeffentlichungspflichtiges;
    }

    public void setVeroeffentlichungspflichtiges(String veroeffentlichungspflichtiges) {
        this.veroeffentlichungspflichtiges = veroeffentlichungspflichtiges;
    }

    public int getWahlperiode() {
        return wahlperiode;
    }

    public void setWahlperiode(int wahlperiode) {
        this.wahlperiode = wahlperiode;
    }

    public Date getMdbwp_von() {
        return mdbwp_von;
    }

    public void setMdbwp_von(Date mdbwp_von) {
        this.mdbwp_von = mdbwp_von;
    }

    public Date getMdbwp_bis() {
        return mdbwp_bis;
    }

    public void setMdbwp_bis(Date mdbwp_bis) {
        this.mdbwp_bis = mdbwp_bis;
    }

    public int getWkr_nummer() {
        return wkr_nummer;
    }

    public void setWkr_nummer(int wkr_nummer) {
        this.wkr_nummer = wkr_nummer;
    }

    public String getWkr_name() {
        return wkr_name;
    }

    public void setWkr_name(String wkr_name) {
        this.wkr_name = wkr_name;
    }

    public String getWkr_land() {
        return wkr_land;
    }

    public void setWkr_land(String wkr_land) {
        this.wkr_land = wkr_land;
    }

    public String getListe() {
        return liste;
    }

    public void setListe(String liste) {
        this.liste = liste;
    }

    public String getMandatsart() {
        return mandatsart;
    }

    public void setMandatsart(String mandatsart) {
        this.mandatsart = mandatsart;
    }

    public String[] getInstitutionen() {
        return institutionen;
    }

    public void setInstitutionen(String[] institutionen) {
        this.institutionen = institutionen;
    }

}
