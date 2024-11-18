package org.texttechnologylab.project.Stud2.data.impl.mongoDB;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.data.BundestagFactory;
import org.texttechnologylab.project.Stud2.data.impl.file.PlenarprotokollImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.WahlperiodeImpl;
import org.texttechnologylab.project.Stud2.exceptions.AbgeordneterNotFoundException;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Eine Klasse für Sitzungen, die aus der MongoDB ausgelesen wurden
 *
 * @author Stud2
 */
public class PlenarprotokollMongoDBImpl extends PlenarprotokollImpl {
    /**
     * Konstruktor für ein Objekt der Klasse Sitzung_MongoDB_Impl
     *
     * @param doc das Dokument, welches die Datenbank für eine Sitzung liefert
     */
    public PlenarprotokollMongoDBImpl(Document doc, BundestagFactory factory){
        super("Sitzung " + doc.getInteger("sitzungsnummer"),
                new WahlperiodeImpl("WP" + doc.getInteger("wahlperiode"), doc.getInteger("wahlperiode"), null, null),
                doc.getString("ort"),
                doc.getInteger("sitzungsnummer"),
                new Date(doc.getDate("datum").getTime()),
                new Date(doc.getDate("beginn").getTime()),
                new Date(doc.getDate("ende").getTime()),
                ((List<Document>) doc.get("reden")).stream().map(rede -> {
                    try {
                        return new RedeMongoDBImpl(rede, factory);
                    } catch (AbgeordneterNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()));
    }
}
