package org.texttechnologylab.project.Stud2.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.exception.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud2.factory.BundestagFactory;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Eine Klasse für Reden, die aus der MongoDB ausgelesen wurden
 *
 * @author Stud2
 */
public class Rede_MongoDB_Impl extends Rede_File_Impl {
    /**
     * Konstruktor für ein Objekt der Klasse Rede_MongoDB_Impl
     *
     * @param doc das Dokument, welches die Datenbank für eine Rede liefert
     */
    public Rede_MongoDB_Impl(Document doc, BundestagFactory factory) throws AbgeordneterNotFoundException {
        super(Integer.parseInt(doc.getString("_id")),
                "ID" + doc.getString("_id"),
                factory.getAbgeordneterByID(Integer.parseInt(doc.getString("rednerID"))),
                doc.getString("textOneLine"),
                new Date((long) doc.get("datum")),
                null);
    }
}
