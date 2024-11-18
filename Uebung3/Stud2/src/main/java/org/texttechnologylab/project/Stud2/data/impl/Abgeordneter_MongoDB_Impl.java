package org.texttechnologylab.project.Stud2.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.data.Mitgliedschaft;
import org.texttechnologylab.project.Stud2.data.Types;

import java.util.List;
import java.util.Set;

/**
 * Eine Klasse für Abgeordnete, die aus der MongoDB ausgelesen wurden
 *
 * @author Stud2
 */
public class Abgeordneter_MongoDB_Impl extends Abgeordneter_File_Impl {
    private Set<Mitgliedschaft> mitgliedschaften;
    private List<Integer> reden;
    private List<Integer> kommentare;

    /**
     * Der Konstruktor für ein Objekt der Klasse Abgeordneter_MongoDB_Impl
     *
     * @param doc das Dokument, welches die Datenbank für einen Abgeordneten liefert
     */
    public Abgeordneter_MongoDB_Impl(Document doc) {
        super(Integer.parseInt(doc.getString("_id")),
                ((Document) doc.get("name")).get("nachname").toString(),
                ((Document) doc.get("name")).get("vorname").toString(),
                ((Document) doc.get("name")).get("ortszusatz").toString(),
                ((Document) doc.get("name")).get("adel").toString(),
                ((Document) doc.get("name")).get("anrede").toString(),
                ((Document) doc.get("name")).get("titel").toString(),
                new java.sql.Date(((java.util.Date) ((Document) doc.get("biografie")).get("geburtsdatum")).getTime()),
                ((Document) doc.get("biografie")).get("geburtsort").toString(),
                ((Document) doc.get("biografie")).get("sterbedatum") == null ? null :
                new java.sql.Date(((java.util.Date) ((Document) doc.get("biografie")).get("sterbedatum")).getTime()),
                ((Document) doc.get("biografie")).get("geschlecht") == null ?
                      null :
                      ((Document) doc.get("biografie")).get("geschlecht").equals("m") ?
                              Types.GESCHLECHT.MAENNLICH :
                              Types.GESCHLECHT.WEIBLICH,
                ((Document) doc.get("biografie")).get("religion").toString(),
                ((Document) doc.get("biografie")).get("beruf").toString(),
                ((Document) doc.get("biografie")).get("vita_kurz").toString(),
                new Partei_Impl(doc.getString("partei")));

        this.mitgliedschaften = null;
        this.reden = (List<Integer>) doc.get("reden");
        this.kommentare = (List<Integer>) doc.get("kommentare");
    }
}
