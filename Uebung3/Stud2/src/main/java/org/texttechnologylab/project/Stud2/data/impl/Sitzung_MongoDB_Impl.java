package org.texttechnologylab.project.Stud2.data.impl;

import org.bson.Document;

import java.sql.Date;

/**
 * Eine Klasse für Sitzungen, die aus der MongoDB ausgelesen wurden
 *
 * @author Stud2
 */
public class Sitzung_MongoDB_Impl extends Sitzung_File_Impl {
    /**
     * Konstruktor für ein Objekt der Klasse Sitzung_MongoDB_Impl
     *
     * @param doc das Dokument, welches die Datenbank für eine Sitzung liefert
     */
    public Sitzung_MongoDB_Impl(Document doc){
        super("Sitzung " + doc.getInteger("sitzungsnummer"),
                new Wahlperiode_Impl("WP" + doc.getInteger("wahlperiode"), doc.getInteger("wahlperiode"), null, null),
                doc.getString("ort"),
                doc.getInteger("sitzungsnummer"),
                new Date((long) doc.get("datum")),
                new Date((Long) doc.get("beginn")),
                new Date((Long) doc.get("ende")));
    }
}
