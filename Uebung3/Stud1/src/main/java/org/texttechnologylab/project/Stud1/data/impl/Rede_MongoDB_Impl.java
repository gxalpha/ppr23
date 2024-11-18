package org.texttechnologylab.project.Stud1.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Stud1.data.BundestagFactory;

/**
 * Rede-Implementation für MongoDB
 */
public class Rede_MongoDB_Impl extends Rede_File_Impl {

    /**
     * Konstruktor
     *
     * @param document Dokument
     */
    Rede_MongoDB_Impl(BundestagFactory factory, Document document) {
        super(
                document.getString(Keys.ID),
                null,
                document.getString(Keys.TEXT),
                document.getList(Keys.KOMMENTARE, String.class),
                document.getDate(Keys.DATUM),
                null
        );
    }

    /**
     * Hilfsklasse zum Vermeiden von Typos.
     */
    public static class Keys {
        public static final String COLLECTION_NAME = "rede";
        public static final String ID = "_id";
        public static final String REDNER = "redner_id";
        public static final String TEXT = "text";
        /* Unfortunately, Mr. [REDACTED] is using MongoDB 3.2 (or possibly 3.3?) which is ancient.
         * Thus, there is neither $strLenCP nor $strLenBytes nor $split so we can't easily find
         * out the length. */
        public static final String TEXT_LENGTH = "text_length";
        public static final String KOMMENTARE = "kommentare";
        public static final String DATUM = "datum";
        public static final String SITZUNG = "sitzung_id";

    }

}
