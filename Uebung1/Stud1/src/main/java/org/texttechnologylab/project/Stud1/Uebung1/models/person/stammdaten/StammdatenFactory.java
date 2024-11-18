package org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten;

public class StammdatenFactory {
    private StammdatenFactory() {
    }

    public static BiografischeAngaben makeBiografischeAngaben(String geburtsdatum, String geburtsort, String geburtsland, String sterbedatum, String geschlecht, String familienstand, String religion, String beruf, String parteiKurz, String vitaKurz, String veroeffentlichungspflichtiges) {
        return new BiografischeAngabenImpl(geburtsdatum, geburtsort, geburtsland, sterbedatum, geschlecht, familienstand, religion, beruf, parteiKurz, vitaKurz, veroeffentlichungspflichtiges);
    }

    public static Institution makeInstitution(String insartLang, String insLang, String mdbinsVon, String mdbinsBis, String fktinsVon, String fktinsBis) {
        return new InstitutionImpl(insartLang, insLang, mdbinsVon, mdbinsBis, fktinsVon, fktinsBis);
    }

    public static Name makeName(String nachname, String vorname, String ortszusatz, String adel, String anredeTitel, String akademischerTitel, String historieVon, String historieBis) {
        return new NameImpl(nachname, vorname, ortszusatz, adel, anredeTitel, akademischerTitel, historieVon, historieBis);
    }

    public static Wahlperiode makeWahlperiode(int wp, String mdbwpVon, String mdbwpBis, int wpNummer, String wkrName, String wkrLand, String liste, String mandatsart, Institution[] institutionen) {
        return new WahlperiodeImpl(wp, mdbwpVon, mdbwpBis, wpNummer, wkrName, wkrLand, liste, mandatsart, institutionen);
    }
}
