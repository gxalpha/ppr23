package org.texttechnologylab.project.Stud1.data.impl;

import org.bson.Document;


/**
 * Abgeordneter-Implementation für MongoDB
 */
public class Abgeordneter_MongoDB_Impl extends Abgeordneter_File_Impl {
    /**
     * Konstruktor
     *
     * @param document Dokument
     */
    public Abgeordneter_MongoDB_Impl(Document document) {
        super(
                document.getString(Keys.ID),
                document.getString(Keys.NACHNAME),
                document.getString(Keys.VORNAME),
                document.getString(Keys.ORTSZUSATZ),
                document.getString(Keys.ADELSSUFFIX),
                document.getString(Keys.ANREDE),
                document.getString(Keys.AKADEMISCHER_TITEL),
                document.getDate(Keys.GEBURTSDATUM),
                document.getString(Keys.GEBURTSORT),
                document.getDate(Keys.STERBEDATUM),
                document.getString(Keys.GESCHLECHT),
                document.getString(Keys.RELIGION),
                document.getString(Keys.BERUF),
                document.getString(Keys.VITA),
                document.getString(Keys.PARTEI),
                document.getString(Keys.FRAKTION)
        );
    }


    /**
     * Hilfsklasse zum Vermeiden von Typos.
     */
    public static class Keys {
        public static final String COLLECTION_NAME = "abgeordneter";
        public static final String ID = "_id";
        public static final String NACHNAME = "nachname";
        public static final String VORNAME = "vorname";
        public static final String ORTSZUSATZ = "ortszusatz";
        public static final String ADELSSUFFIX = "adelssuffix";
        public static final String ANREDE = "anrede";
        public static final String AKADEMISCHER_TITEL = "akademischerTitel";
        public static final String GEBURTSDATUM = "geburtsdatum";
        public static final String GEBURTSORT = "geburtsort";
        public static final String STERBEDATUM = "sterbedatum";
        public static final String GESCHLECHT = "geschlecht";
        public static final String RELIGION = "religion";
        public static final String BERUF = "beruf";
        public static final String VITA = "vita";
        public static final String PARTEI = "partei";
        public static final String FRAKTION = "fraktion";
        public static final String REDEN = "reden_ids";
    }
}
