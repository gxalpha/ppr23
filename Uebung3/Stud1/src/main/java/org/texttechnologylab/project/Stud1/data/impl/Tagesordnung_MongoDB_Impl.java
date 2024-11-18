package org.texttechnologylab.project.Stud1.data.impl;

import org.bson.Document;

import java.util.HashSet;

/**
 * Tagesordnung-Implementation für MongoDB
 */
public class Tagesordnung_MongoDB_Impl extends Tagesordnung_File_Impl {
    /**
     * Konstruktor
     *
     * @param document Dokument
     */
    public Tagesordnung_MongoDB_Impl(Document document) {
        super(
                null,
                new HashSet<>(document.getList(Keys.TAGESORDNUNGSPUNKTE, String.class))
        );
    }

    /**
     * Hilfsklasse zum Vermeiden von Typos.
     */
    public static class Keys {
        public static final String COLLECTION_NAME = "tagesordnung";
        public static final String ID = "_id";
        public static final String TAGESORDNUNGSPUNKTE = "tagesordnungspunkte";
    }
}
