package org.texttechnologylab.project.Stud2.impl;

import java.util.Date;

// Contains all attributes a Gast, Abgeordneter, PersonalBundestagsverwaltung and Mitarbeiter have in common:
public abstract class Person {
    protected String nachname_vorname;
    protected String email;
    protected String ortszusatz;
    protected String adel;
    protected String praefix;
    protected String anrede_titel;
    protected String akad_titel;
    protected Date geburtsdatum;
    protected String geburtsort;
    protected String geburtsland;
    protected Date sterbedatum;
    protected String geschlecht;
    protected String familienstand;
    protected String religion;
    protected String beruf;

    // Getter and Setter
    public String getNachname_vorname() {
        return nachname_vorname;
    }

    public void setNachname_vorname(String nachname_vorname) {
        this.nachname_vorname = nachname_vorname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrtszusatz() {
        return ortszusatz;
    }

    public void setOrtszusatz(String ortszusatz) {
        this.ortszusatz = ortszusatz;
    }

    public String getAdel() {
        return adel;
    }

    public void setAdel(String adel) {
        this.adel = adel;
    }

    public String getPraefix() {
        return praefix;
    }

    public void setPraefix(String praefix) {
        this.praefix = praefix;
    }

    public String getAnrede_titel() {
        return anrede_titel;
    }

    public void setAnrede_titel(String anrede_titel) {
        this.anrede_titel = anrede_titel;
    }

    public String getAkad_titel() {
        return akad_titel;
    }

    public void setAkad_titel(String akad_titel) {
        this.akad_titel = akad_titel;
    }

    public Date getGeburtsdatum() {
        return geburtsdatum;
    }

    public void setGeburtsdatum(Date geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public String getGeburtsort() {
        return geburtsort;
    }

    public void setGeburtsort(String geburtsort) {
        this.geburtsort = geburtsort;
    }

    public String getGeburtsland() {
        return geburtsland;
    }

    public void setGeburtsland(String geburtsland) {
        this.geburtsland = geburtsland;
    }

    public Date getSterbedatum() {
        return sterbedatum;
    }

    public void setSterbedatum(Date sterbedatum) {
        this.sterbedatum = sterbedatum;
    }

    public String getGeschlecht() {
        return geschlecht;
    }

    public void setGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
    }

    public String getFamilienstand() {
        return familienstand;
    }

    public void setFamilienstand(String familienstand) {
        this.familienstand = familienstand;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getBeruf() {
        return beruf;
    }

    public void setBeruf(String beruf) {
        this.beruf = beruf;
    }
}
