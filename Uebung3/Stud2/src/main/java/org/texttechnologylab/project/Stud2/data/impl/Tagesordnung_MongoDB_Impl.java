package org.texttechnologylab.project.Stud2.data.impl;

import org.bson.Document;

/**
 * Eine Klasse für Tagesordnungen, die aus der MongoDB ausgelesen wurden
 *  - eine Tagesordnung besteht aus mehreren Tagesordnungspunkten, daher noch eine Klasse Tagesordnungspunkt_Impl
 *
 * @author Stud2
 */
public class Tagesordnung_MongoDB_Impl extends Tagesordnung_File_Impl {
    /**
     * Konstruktor für ein Objekt der Klasse Tagesordnung_MongoDB_Impl
     *
     * @param doc das Dokument, das die Tagesordnung in der DB repräsentiert
     */
    public Tagesordnung_MongoDB_Impl(Document doc) {
        super("Tagesordnung der " + doc.getInteger("sitzung") + ". Sitzung", null, null);
    }
}
