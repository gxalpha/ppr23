package org.texttechnologylab.project.Stud2.data.impl.mongoDB;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.data.impl.file.AbgeordneterImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.MandatImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.ParteiImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.WahlkreisImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.WahlperiodeImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * Eine Klasse für Abgeordnete, die aus der MongoDB ausgelesen wurden
 *
 * @author Stud2
 */
public class AbgeordneterMongoDBImpl extends AbgeordneterImpl {

    /**
     * Der Konstruktor für ein Objekt der Klasse Abgeordneter_MongoDB_Impl
     *
     * @param doc das Dokument, welches die Datenbank für einen Abgeordneten liefert
     */
    public AbgeordneterMongoDBImpl(Document doc) {
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
                new ParteiImpl(doc.getString("partei")));

        this.mandate = new ArrayList<>();

        for (String wahlperiode : ((Document) doc.get("wahlperioden")).keySet()) {
            MandatImpl mandat = new MandatImpl(
                    wahlperiode,
                    this,
                    ((Document) ((Document) doc.get("wahlperioden")).get(wahlperiode)).get("mdwp_von") == null ? null :
                            new java.sql.Date(((java.util.Date) ((Document) ((Document) doc.get("wahlperioden")).get(wahlperiode)).get("mdwp_von")).getTime()),
                    ((Document) ((Document) doc.get("wahlperioden")).get(wahlperiode)).get("mdwp_bis") == null ? null :
                            new java.sql.Date(((java.util.Date) ((Document) ((Document) doc.get("wahlperioden")).get(wahlperiode)).get("mdwp_bis")).getTime()),
                    ((Document) ((Document) doc.get("wahlperioden")).get(wahlperiode)).get("mandatsart").equals("Landesliste") ?
                            Types.MANDAT.LANDESLISTE : ((Document) ((Document) doc.get("wahlperioden")).get(wahlperiode)).get("mandatsart").equals("Direktwahl") ?
                            Types.MANDAT.DIREKTWAHL : Types.MANDAT.VOLKSKAMMER,
                    new WahlperiodeImpl(wahlperiode, Integer.parseInt(wahlperiode.substring(2)), null, null),
                    new WahlkreisImpl("DummyWahlkreis", ((Document) ((Document) doc.get("wahlperioden")).get(wahlperiode)).getInteger("wahlkreis")),
                    ((Document) ((Document) doc.get("wahlperioden")).get(wahlperiode)).getString("liste")
            );

            mandat.setGruppen((List<String>) ((Document) ((Document) doc.get("wahlperioden")).get(wahlperiode)).get("gruppen"));

            this.mandate.add(mandat);
        }

        this.redenIDs = (List<String>) doc.get("reden");

    }
}
