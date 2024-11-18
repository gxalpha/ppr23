package org.texttechnologylab.project.Stud1.database;

import org.bson.Document;
import org.texttechnologylab.project.Stud1.data.Abgeordneter;
import org.texttechnologylab.project.Stud1.data.Rede;
import org.texttechnologylab.project.Stud1.data.Sitzung;
import org.texttechnologylab.project.Stud1.data.Tagesordnung;
import org.texttechnologylab.project.Stud1.data.impl.Abgeordneter_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.Rede_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.Sitzung_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.Tagesordnung_MongoDB_Impl;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Hilfsklasse zum Erstellen von Dokumenten
 */
class MongoDBConversionHelper {

    /**
     * Bildet einen Abgeordneten auf ein Document ab
     *
     * @param abgeordneter Der Abgeordnete
     * @return Das Dokument
     */
    public static Document toDocument(Abgeordneter abgeordneter) {
        Document document = new Document();
        document.append(Abgeordneter_MongoDB_Impl.Keys.ID, abgeordneter.getID());
        document.append(Abgeordneter_MongoDB_Impl.Keys.NACHNAME, abgeordneter.getNachname());
        document.append(Abgeordneter_MongoDB_Impl.Keys.VORNAME, abgeordneter.getVorname());
        document.append(Abgeordneter_MongoDB_Impl.Keys.ORTSZUSATZ, abgeordneter.getOrtszusatz());
        document.append(Abgeordneter_MongoDB_Impl.Keys.ADELSSUFFIX, abgeordneter.getAdelssuffix());
        document.append(Abgeordneter_MongoDB_Impl.Keys.ANREDE, abgeordneter.getAnrede());
        document.append(Abgeordneter_MongoDB_Impl.Keys.AKADEMISCHER_TITEL, abgeordneter.getAkademischerTitel());
        document.append(Abgeordneter_MongoDB_Impl.Keys.GEBURTSDATUM, abgeordneter.getGeburtsdatum());
        document.append(Abgeordneter_MongoDB_Impl.Keys.GEBURTSORT, abgeordneter.getGeburtsort());
        document.append(Abgeordneter_MongoDB_Impl.Keys.STERBEDATUM, abgeordneter.getSterbedatum());
        document.append(Abgeordneter_MongoDB_Impl.Keys.GESCHLECHT, abgeordneter.getGeschlecht());
        document.append(Abgeordneter_MongoDB_Impl.Keys.RELIGION, abgeordneter.getReligion());
        document.append(Abgeordneter_MongoDB_Impl.Keys.BERUF, abgeordneter.getBeruf());
        document.append(Abgeordneter_MongoDB_Impl.Keys.VITA, abgeordneter.getVita());
        document.append(Abgeordneter_MongoDB_Impl.Keys.PARTEI, abgeordneter.getPartei());
        document.append(Abgeordneter_MongoDB_Impl.Keys.FRAKTION, abgeordneter.getFraktion());
        document.append(Abgeordneter_MongoDB_Impl.Keys.REDEN, abgeordneter.getReden().stream().map(Rede::getID).collect(Collectors.toList()));
        return document;
    }

    /**
     * Bildet eine Rede auf ein Document ab
     *
     * @param rede Die Rede
     * @return Das Dokument
     */
    public static Document toDocument(Rede rede) {
        Document document = new Document();
        document.append(Rede_MongoDB_Impl.Keys.ID, rede.getID());
        document.append(Rede_MongoDB_Impl.Keys.REDNER, rede.getRedner().getID());
        document.append(Rede_MongoDB_Impl.Keys.TEXT, rede.getText());
        document.append(Rede_MongoDB_Impl.Keys.TEXT_LENGTH, rede.getText().split(" ").length);
        document.append(Rede_MongoDB_Impl.Keys.KOMMENTARE, rede.getKommentare());
        document.append(Rede_MongoDB_Impl.Keys.DATUM, rede.getDate());
        document.append(Rede_MongoDB_Impl.Keys.SITZUNG, rede.getSitzung().getID());
        return document;
    }

    /**
     * Bildet eine Sitzung auf ein Document ab
     *
     * @param sitzung Die Sitzung
     * @return Das Dokument
     */
    public static Document toDocument(Sitzung sitzung) {
        Document document = new Document();
        document.append(Sitzung_MongoDB_Impl.Keys.ID, sitzung.getID());
        document.append(Sitzung_MongoDB_Impl.Keys.DATUM, sitzung.getDate());
        document.append(Sitzung_MongoDB_Impl.Keys.DAUER, sitzung.getDauer());
        document.append(Sitzung_MongoDB_Impl.Keys.REDEN, sitzung.getReden().stream().map(Rede::getID).collect(Collectors.toList()));
        return document;
    }

    /**
     * Bildet eine Tagesordnung auf ein Document ab
     *
     * @param tagesordnung Die Tagesordnung
     * @return Das Dokument
     */
    public static Document toDocument(Tagesordnung tagesordnung) {
        Document document = new Document();
        document.append(Tagesordnung_MongoDB_Impl.Keys.ID, tagesordnung.getID());
        document.append(Tagesordnung_MongoDB_Impl.Keys.TAGESORDNUNGSPUNKTE, new ArrayList<>(tagesordnung.getTagesordnungspunkte()));
        return document;
    }

}
