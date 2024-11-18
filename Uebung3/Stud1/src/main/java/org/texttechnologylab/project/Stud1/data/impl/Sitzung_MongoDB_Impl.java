package org.texttechnologylab.project.Stud1.data.impl;

import org.bson.Document;

/**
 * Rede-Implementation für MongoDB
 */
public class Sitzung_MongoDB_Impl extends Sitzung_File_Impl {
    /**
     * Konstruktor
     *
     * @param document Dokument
     */
    public Sitzung_MongoDB_Impl(Document document) {
        super(
                document.getDate(Keys.DATUM)
        );
        super.setDauer(document.getInteger(Keys.DAUER));
    }

    /**
     * Hilfsklasse zum Vermeiden von Typos.
     */
    public static class Keys {
        public static final String COLLECTION_NAME = "sitzung";
        public static final String ID = "_id";
        public static final String DATUM = "datum";
        public static final String DAUER = "dauer";
        public static final String REDEN = "rede_ids";
    }
}
