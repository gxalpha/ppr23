package org.texttechnologylab.project.Stud2.data.impl;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UnwindOptions;
import org.bson.conversions.Bson;
import org.texttechnologylab.project.Stud2.data.Abgeordneter;
import org.texttechnologylab.project.Stud2.data.Ausschuss;
import org.texttechnologylab.project.Stud2.data.BundestagFactory;
import org.texttechnologylab.project.Stud2.data.Fraktion;
import org.texttechnologylab.project.Stud2.data.Gruppe;
import org.texttechnologylab.project.Stud2.data.Mandat;
import org.texttechnologylab.project.Stud2.data.Mitgliedschaft;
import org.texttechnologylab.project.Stud2.data.Partei;
import org.texttechnologylab.project.Stud2.data.Plenarprotokoll;
import org.texttechnologylab.project.Stud2.data.Rede;
import org.texttechnologylab.project.Stud2.data.Wahlkreis;
import org.texttechnologylab.project.Stud2.data.Wahlperiode;
import org.texttechnologylab.project.Stud2.data.impl.file.AbgeordneterImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.AusschussImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.FraktionImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.GruppeImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.MandatImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.MitgliedschaftImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.ParteiImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.PlenarprotokollImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.RedeImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.Types;
import org.texttechnologylab.project.Stud2.data.impl.file.WahlkreisImpl;
import org.texttechnologylab.project.Stud2.data.impl.file.WahlperiodeImpl;
import org.texttechnologylab.project.Stud2.data.impl.mongoDB.AbgeordneterMongoDBImpl;
import org.texttechnologylab.project.Stud2.data.impl.mongoDB.RedeMongoDBImpl;
import org.texttechnologylab.project.Stud2.database.MongoDBConnectionHandler;
import org.texttechnologylab.project.Stud2.exceptions.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud2.exceptions.BundestagException;
import org.texttechnologylab.project.Stud2.exceptions.RedeNotFoundException;
import org.texttechnologylab.project.Stud2.helper.StringHelper;
import org.texttechnologylab.project.Stud2.helper.Tripple;
import org.texttechnologylab.project.Stud2.helper.Tuple;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Klasse für eine BundestagFactory, über die alle Bundestag-Objekte erzeugt werden sollen
 *
 * @author Stud2
 */
public class BundestagFactoryImpl implements BundestagFactory {

    // Ergebnisse des Einlesens der XML-Dateien werden in folgenden Datenstrukturen gespeichert:
    private Set<Abgeordneter> abgeordnete = null;
    private Set<Fraktion> fraktionen = null;
    private Set<Partei> parteien = new HashSet<>();
    private Set<Mitgliedschaft> mitgliedschaften = null;
    private List<Wahlperiode> wahlperioden = new ArrayList<>();
    private List<Plenarprotokoll> plenarprotokolle = null;
    private List<Rede> reden = new ArrayList<>();

    // MongoDB-Objekte werden ebenfalls in der Factory gespeichert:
    private MongoDBConnectionHandler mongoDB;
    private List<AbgeordneterMongoDBImpl> abgeordneteDB;
    private Map<String, RedeMongoDBImpl> redenDB;

    /**
     * Konstruktor für eine BundestagFactory
     *
     * @param pathMDB_Stammdaten    der Pfad zur einzulesenden MDB_STAMMDATEN.XML-Datei
     * @param pathBundestagsreden20 der Pfad zum ORDNER mit den XML-Sitzungsprotokollen
     */
    public BundestagFactoryImpl(String pathMDB_Stammdaten, String pathBundestagsreden20,
                                boolean parseAndUploadDataInMongoDB,
                                MongoDBConnectionHandler mongoDB)
            throws Exception {

        this.mongoDB = mongoDB;

        // Optional: Die Daten neu einlesen und in der MondoDB hochladen
        // ACHTUNG:  Alle CAS-Einträge gehen dann verloren und die Reden müssen neu analysiert werden!
        if (parseAndUploadDataInMongoDB) {

            // MDB_Stammdaten.XML parsen
            Tripple<Set<Abgeordneter>, Set<Fraktion>, Set<Mitgliedschaft>> parseResult = parseAbgeordnete(pathMDB_Stammdaten);
            System.out.println("\n" + StringHelper.getCurrDateTimeFormatted() + " Parsing MDB_STAMMDATEN.XML ...");

            this.abgeordnete = parseResult.getFirst();
            this.fraktionen = parseResult.getSecond();
            this.mitgliedschaften = parseResult.getThird();

            // Ordner Bundestagsreden20 mit den XML-Sitzungsprotokollen parsen

            List<Plenarprotokoll> alleSitzungen = new ArrayList<>();

            // Ist der Pfad leer, so wird keine Rede eingelesen
            if (!pathBundestagsreden20.isEmpty()) {
                File[] sitzungen = new File(pathBundestagsreden20).listFiles();
                assert sitzungen != null;

                List<Tuple<File, Integer>> filesSorted = new ArrayList<>();

                for (File sitzung : sitzungen) {
                    if (sitzung.getName().endsWith(".xml")) {
                        filesSorted.add(new Tuple<>(sitzung, Integer.parseInt(sitzung.getName().split("\\.")[0])));
                    }
                }

                filesSorted.sort(Comparator.comparing(Tuple::getSecond));

                for (Tuple<File, Integer> sitzung : filesSorted) {
                    if (sitzung.getFirst().getName().endsWith(".xml")) {
                        alleSitzungen.add(parseSitzung(sitzung.getFirst()));
                    }
                }
            }

            this.plenarprotokolle = alleSitzungen;

            // Eingelesene Daten in der MongoDB hochladen
            mongoDB.storeDataInMongoDB(this);
        }

        // MongoDB-Objekte (Abgeordnete sowie Reden) erzeugen
        this.abgeordneteDB = mongoDB.initAbgeordneteMongoDB();
        this.redenDB = mongoDB.initRedenMongoDB(this);
    }

    /**
     * Liest MDB_STAMMDATEN.XML ein
     *
     * @param path Der Pfad zur einzulesenden MDB_STAMMDATEN.XML-Datei
     * @return Ein 3-Tupel der Form (alle Abgeordneten, alle Fraktionen, alle Mitgliedschaften)
     */
    @Override
    public Tripple<Set<Abgeordneter>, Set<Fraktion>, Set<Mitgliedschaft>> parseAbgeordnete(String path)
            throws ParserConfigurationException, IOException, SAXException, ParseException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // Liste aller Abgeordneten
        List<Abgeordneter> alleAbgeordneten = new ArrayList<>();

        // Liste aller Fraktionen
        List<Fraktion> alleFraktionen = new ArrayList<>();
        alleFraktionen.add(new FraktionImpl("Fraktionslos"));
        alleFraktionen.add(new FraktionImpl("Fraktion BÜNDNIS 90/DIE GRÜNEN"));

        // Alle Mitgliedschaften inklusive Fraktionen, Ausschüsse und sonstigen Gruppierungen
        Set<Mitgliedschaft> alleMitgliedschaften = new HashSet<>();

        // Liste aller Parteien
        List<Partei> alleParteien = new ArrayList<>();
        alleParteien.add(new ParteiImpl("Plos"));
        alleParteien.add(new ParteiImpl("BÜNDNIS 90/DIE GRÜNEN"));

        // Alle Ausschüsse
        Set<Ausschuss> alleAusschuesse = new HashSet<>();

        // Alle sonstigen Gruppen, d.h. ohne Parteien, Fraktionen und Ausschüssen
        Set<Gruppe> sonstGruppen = new HashSet<>();

        // Alle Wahlperioden
        List<Wahlperiode> alleWahlperioden = new ArrayList<>();

        // Alle Wahlkreise
        List<Wahlkreis> alleWahlkreise = new ArrayList<>();
        alleWahlkreise.add(new WahlkreisImpl("k. A.", -1));

        // MDB_STAMMDATEN.XML parsen

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document mdbStammdaten = db.parse(new File(path));

        NodeList mdbs = mdbStammdaten.getElementsByTagName("MDB"); // Enthält alle Abgeordneten-Knoten

        for (int i = 0; i < mdbs.getLength(); i++) {
            Element mdb = (Element) mdbs.item(i); // i-ter Abgeordneter des Bundestags

            // Details zum NAMEN eines Abgeordneten einlesen

            Element namen = (Element) mdb.getElementsByTagName("NAMEN").item(0);
            int sizeNamen = namen.getElementsByTagName("NAME").getLength();
            // Wir nehmen nur den letzten Eintrag mit dem Tag "NAME", da dieser der aktuellste ist
            Element name = (Element) namen.getElementsByTagName("NAME").item(sizeNamen - 1);

            int mdbID = StringHelper.toNaturalNumber(mdb.getElementsByTagName("ID").item(0).getTextContent());
            String nachname = name.getElementsByTagName("NACHNAME").item(0).getTextContent();
            String vorname = name.getElementsByTagName("VORNAME").item(0).getTextContent();
            String ortszusatz = name.getElementsByTagName("ORTSZUSATZ").item(0).getTextContent();
            String adel = name.getElementsByTagName("ADEL").item(0).getTextContent();
            String anredeTitel = name.getElementsByTagName("ANREDE_TITEL").item(0).getTextContent();
            String akadTitel = name.getElementsByTagName("AKAD_TITEL").item(0).getTextContent();

            // BIOGRAFISCHE ANGABEN eines Abgeordneten einlesen

            Element bio = (Element) mdb.getElementsByTagName("BIOGRAFISCHE_ANGABEN").item(0);

            Date geburtsdatum = StringHelper.toDate(bio.getElementsByTagName("GEBURTSDATUM").item(0).getTextContent());
            String geburtsort = bio.getElementsByTagName("GEBURTSORT").item(0).getTextContent();
            Date sterbedatum = StringHelper.toDate(bio.getElementsByTagName("STERBEDATUM").item(0).getTextContent());

            String prov_geschlecht = bio.getElementsByTagName("GESCHLECHT").item(0).getTextContent();
            Types.GESCHLECHT geschlecht;
            if (prov_geschlecht.equals("männlich")) {
                geschlecht = Types.GESCHLECHT.MAENNLICH;
            } else {
                geschlecht = Types.GESCHLECHT.WEIBLICH;
            }

            String religion = bio.getElementsByTagName("RELIGION").item(0).getTextContent();
            String beruf = bio.getElementsByTagName("BERUF").item(0).getTextContent();
            String vita = bio.getElementsByTagName("VITA_KURZ").item(0).getTextContent();

            String prov_partei = bio.getElementsByTagName("PARTEI_KURZ").item(0).getTextContent();
            Partei partei = alleParteien.get(0); // Per Default ist der Abgeordnete parteilos

            // Neue PARTEI des Abgeordneten initialisieren und in alleParteien eingefügen, WENN noch nicht vorhanden

            boolean partei_exists = false;

            for (Partei p : alleParteien) {
                if (prov_partei.equals("DIE GRÜNEN/BÜNDNIS 90") || p.getLabel().equals(prov_partei)) {
                    partei_exists = true;
                    break;
                }
            }

            if (!partei_exists && !prov_partei.isEmpty()) {
                partei = new ParteiImpl(prov_partei);
                alleParteien.add(partei);
            } else {
                // Ansonsten passende Partei aus alleParteien heraussuchen und zuweisen, da schon vorhanden
                for (Partei p_i : alleParteien) {
                    if (p_i.getLabel().equals(prov_partei)
                            // Wir nehmen an, dass die folgenden beiden Fraktionen äquivalent sind:
                            || (p_i.getLabel().equals("BÜNDNIS 90/DIE GRÜNEN")
                            && prov_partei.equals("DIE GRÜNEN/BÜNDNIS 90"))) {
                        partei = p_i;
                        break;
                    }
                }
            }

            // ABGEORDNETEN initialisieren und in die Liste aller Abgeordneten sowie in die Partei hinzufügen
            // Achtung: Mandate, Mitgliedschaften (und Abstimmungen) werden nachträglich hinzugefügt

            Abgeordneter abgeordneter = new AbgeordneterImpl(mdbID, nachname, vorname, ortszusatz, adel, anredeTitel,
                    akadTitel, geburtsdatum, geburtsort, sterbedatum, geschlecht, religion, beruf, vita, partei);

            alleAbgeordneten.add(abgeordneter);
            partei.addMember(abgeordneter);

            // Alle WAHLPERIODEN des Abgeordneten einlesen, in alleWahlperioden abspeichern und alle darin enthaltenen
            // MANDATE des Abgeordneten erfassen

            Element wahlperioden = (Element) mdb.getElementsByTagName("WAHLPERIODEN").item(0);
            NodeList wpn = wahlperioden.getElementsByTagName("WAHLPERIODE");

            for (int j = 0; j < wpn.getLength(); j++) {
                Element wp = (Element) wpn.item(j); // j-te Wahlperiode des Abgeordneten

                int nummer = StringHelper.toNaturalNumber(wp.getElementsByTagName("WP").item(0).getTextContent());
                Date startDate = StringHelper.toDate(wp.getElementsByTagName("MDBWP_VON").item(0).getTextContent());
                Date endDate = StringHelper.toDate(wp.getElementsByTagName("MDBWP_BIS").item(0).getTextContent());

                // WAHLPERIODE initialisieren und in alleWahlperioden eingefügen, WENN noch nicht vorhanden

                Wahlperiode wahlperiode = null;

                boolean wahlperiode_exists = false;

                for (Wahlperiode p : alleWahlperioden) {
                    if (p.getNumber() == nummer) {
                        wahlperiode_exists = true;
                        break;
                    }
                }

                if (!wahlperiode_exists) {
                    wahlperiode = new WahlperiodeImpl("Wahlperiode" + nummer, nummer, startDate, endDate);
                    alleWahlperioden.add(wahlperiode);
                } else {
                    // Ansonsten passende Wahlperiode aus alleWahlperioden zuweisen, da schon vorhanden
                    for (Wahlperiode w_i : alleWahlperioden) {
                        if (w_i.getNumber() == nummer) {
                            wahlperiode = w_i;
                            break;
                        }
                    }
                }

                // Neuen WAHLKREIS initialisieren und in alleWahlkreise hinzufügen, WENN nicht vorhanden

                int wahlkreisNummer = StringHelper.toNaturalNumber(wp.getElementsByTagName("WKR_NUMMER").item(0).getTextContent());

                Wahlkreis wahlkreis = alleWahlkreise.get(0); // Default: ohne Angabe, Wahlkreisnummer = -1

                boolean wahlkreis_exists = false;

                for (Wahlkreis wk : alleWahlkreise) {
                    if (wk.getNumber() == wahlkreisNummer) {
                        wahlkreis_exists = true;
                        break;
                    }
                }

                // -1 als Wahlkreisnummer bedeutet hier, dass der Eintrag leer war → kein Wahlkreis erzeugen
                if (!wahlkreis_exists && wahlkreisNummer != -1) {
                    wahlkreis = new WahlkreisImpl("Wahlkreis " + wahlkreisNummer, wahlkreisNummer);
                    alleWahlkreise.add(wahlkreis);
                } else {
                    // Ansonsten passenden Wahlkreis aus alleWahlkreise und zuweisen, da schon vorhanden:
                    for (Wahlkreis w_i : alleWahlkreise) {
                        if (w_i.getNumber() == wahlkreisNummer) {
                            wahlkreis = w_i;
                            break;
                        }
                    }
                }

                // MANDATSART dieses Abgeordneten innerhalb dieser Wahlperiode einlesen

                String prov_typ = wp.getElementsByTagName("MANDATSART").item(0).getTextContent();
                String liste = wp.getElementsByTagName("LISTE").item(0).getTextContent();
                Types.MANDAT typ;

                if (prov_typ.equals("Landesliste")) {
                    typ = Types.MANDAT.LANDESLISTE;
                } else if (prov_typ.equals("Direktwahl")) {
                    typ = Types.MANDAT.DIREKTWAHL;
                } else {
                    typ = Types.MANDAT.VOLKSKAMMER;
                }

                // MANDAT initialisieren und zum Set aller Mandate dieses Abgeordneten hinzufügen
                // Achtung: Fraktionen, Ausschüsse und sonstige Mitgliedschaften werden im Nachhinein dem Objekt hinzugefügt

                Mandat mandat = new MandatImpl("MANDAT-" + abgeordneter.getLabel() + "-WP" + nummer,
                        abgeordneter, startDate, endDate, typ, wahlperiode, wahlkreis, liste);

                // INSTITUTIONEN einlesen, d.h.
                // → FRAKTIONEN des Abgeordneten während dieses Mandates einlesen
                // → AUSSCHÜSSE des Abgeordneten während dieses Mandates einlesen
                // → sonstige MITGLIEDSCHAFTEN des Abgeordneten während dieses Mandates einlesen

                NodeList institutionen = wp.getElementsByTagName("INSTITUTION");

                for (int k = 0; k < institutionen.getLength(); k++) {
                    Element inst = (Element) institutionen.item(k); // k-te Institution des Abgeordneten während dieses Mandates

                    String insArt = inst.getElementsByTagName("INSART_LANG").item(0).getTextContent();

                    String insLang = inst.getElementsByTagName("INS_LANG").item(0).getTextContent();

                    String funktion = inst.getElementsByTagName("FKT_LANG").item(0).getTextContent();
                    if (funktion.isEmpty()) {
                        funktion = "k. A.";
                    }
                    Date mitgliedVon = StringHelper.toDate(inst.getElementsByTagName("MDBINS_VON").item(0).getTextContent());
                    Date mitgliedBis = StringHelper.toDate(inst.getElementsByTagName("MDBINS_BIS").item(0).getTextContent());

                    // Alle MITGLIEDSCHAFTEN des Abgeordneten erfassen

                    Mitgliedschaft mitgliedschaft = null;

                    switch (insArt) {
                        case "Ausschuss":
                        case "Unterausschuss":
                        case "Untersuchungsausschuss":
                            Ausschuss ausschuss = null;

                            // Neuen AUSSCHUSS initialisieren, in alleAusschuesse sowie in das Mandat einfügen, WENN noch nicht vorhanden

                            boolean ausschuss_exists = false;

                            for (Ausschuss a : alleAusschuesse) {
                                if (insLang.equals(a.getLabel())) {
                                    ausschuss_exists = true;
                                    break;
                                }
                            }

                            if (!ausschuss_exists) {
                                ausschuss = new AusschussImpl(insLang, insArt);
                                alleAusschuesse.add(ausschuss);
                            } else {
                                // Ansonsten passenden Ausschuss aus alleAusschuesse zuweisen, da schon vorhanden:
                                for (Ausschuss a_i : alleAusschuesse) {
                                    if (a_i.getLabel().equals(insLang)) {
                                        ausschuss = a_i;
                                        break;
                                    }
                                }
                            }
                            mitgliedschaft = new MitgliedschaftImpl(abgeordneter, funktion, mitgliedVon, mitgliedBis, ausschuss, wahlperiode);
                            mandat.addAusschuss(ausschuss);

                            // Abgeordneten zum Ausschuss hinzufügen
                            if (ausschuss != null) {
                                ausschuss.addMember(abgeordneter);
                            }
                            break;

                        case "Deutscher Bundestag":
                        case "Ministerium":
                        case "Enquete-Kommission":
                        case "Parlamentariergruppen":
                        case "Sonstiges Gremium":
                            Gruppe gruppe = null;

                            // Neue GRUPPE initialisieren und in sonstGruppen einfügen, WENN noch nicht vorhanden

                            boolean gruppe_exists = false;

                            for (Gruppe a : sonstGruppen) {
                                if (insLang.equals(a.getLabel())) {
                                    gruppe_exists = true;
                                    break;
                                }
                            }

                            if (!gruppe_exists) {
                                gruppe = new GruppeImpl(insLang);
                                sonstGruppen.add(gruppe);
                            } else {
                                // Ansonsten passende Gruppe aus sonstGruppen zuweisen, da schon vorhanden:
                                for (Gruppe a_i : sonstGruppen) {
                                    if (a_i.getLabel().equals(insLang)) {
                                        gruppe = a_i;
                                        break;
                                    }
                                }
                            }
                            mitgliedschaft = new MitgliedschaftImpl(abgeordneter, funktion, mitgliedVon, mitgliedBis, gruppe, wahlperiode);

                            if (gruppe != null) {
                                gruppe.addMember(abgeordneter);
                            }
                            break;

                        case "Fraktion/Gruppe":
                            Fraktion fraktion = alleFraktionen.get(0); // Per Default ist der Abgeordnete fraktionslos

                            // Neue FRAKTION initialisieren und in alleFraktionen einfügen, WENN noch nicht vorhanden

                            boolean fraktion_exists = false;

                            for (Fraktion a : alleFraktionen) {
                                if (insLang.equals(a.getLabel()) || insLang.equals("Fraktion Die Grünen/Bündnis 90")) {
                                    fraktion_exists = true;
                                    break;
                                }
                            }

                            if (!fraktion_exists) {
                                fraktion = new FraktionImpl(insLang);
                                alleFraktionen.add(fraktion);
                            } else {
                                // Ansonsten passende Fraktion aus alleFraktionen heraussuchen, da schon vorhanden:
                                for (Fraktion a_i : alleFraktionen) {
                                    if (a_i.getLabel().equals(insLang)
                                            // Wir nehmen an, dass die folgenden beiden Fraktionen äquivalent sind
                                            || (a_i.getLabel().equals("Fraktion BÜNDNIS 90/DIE GRÜNEN")
                                            && insLang.equals("Fraktion Die Grünen/Bündnis 90"))) {
                                        fraktion = a_i;
                                        break;
                                    }
                                }
                            }
                            mitgliedschaft = new MitgliedschaftImpl(abgeordneter, funktion, mitgliedVon, mitgliedBis, fraktion, wahlperiode);
                            mandat.addFraktion(fraktion);
                            fraktion.addMember(abgeordneter); // Abgeordneten und seine Funktion zur Fraktion hinzufügen

                            if (!funktion.equals("k. A.") && wahlperiode != null) {
                                fraktion.addFunktionstraeger(funktion + " (WP" + wahlperiode.getNumber() + ")", abgeordneter);
                            }
                            break;
                    }

                    // Die im obigen Switch-Case initialisierte Mitgliedschaft den passenden Objekten hinzufügen:
                    if (mitgliedschaft != null) {
                        abgeordneter.addMitgliedschaft(mitgliedschaft);
                        mandat.addMitgliedschaft(mitgliedschaft);
                        alleMitgliedschaften.add(mitgliedschaft);
                    }
                }

                // Die PARTEIZUGEHÖRIGKEIT zählt auch zu einer Mitgliedschaft.
                // Hierbei wird die Parteizugehörigkeit in Wahlperioden unterteilt. Außerhalb der Wahlperioden können
                // wir über die Partei keine Aussage treffen.
                MitgliedschaftImpl mitgliedPartei = new MitgliedschaftImpl(abgeordneter, "Mitglied in WP" +
                        wahlperiode.getNumber(), wahlperiode.getStartDate(), wahlperiode.getEndDate(), partei, wahlperiode);

                abgeordneter.addMitgliedschaft(mitgliedPartei);
                mandat.addMitgliedschaft(mitgliedPartei);
                alleMitgliedschaften.add(mitgliedPartei);

                // Das Mandat kann nun dem Abgeordneten, der Wahlperiode und dem Wahlkreis hinzugefügt werden
                abgeordneter.addMandat(mandat);
                wahlperiode.addMandat(mandat);
                if (wahlkreis != null) {
                    wahlkreis.addMandat(mandat);
                }
            }
        }
        alleWahlperioden.sort(Comparator.comparing(Wahlperiode::getNumber));
        this.wahlperioden.addAll(alleWahlperioden);
        this.parteien.addAll(alleParteien);

        // Umwandeln der zurückzugebenden ArrayLists zu HashSets und Rückgabe des Ergebnisses
        Set<Abgeordneter> alleAbgeordnetenFinal = new HashSet<>(alleAbgeordneten);
        Set<Fraktion> alleFraktionenFinal = new HashSet<>(alleFraktionen);
        Set<Mitgliedschaft> alleMitgliedschaftenFinal = new HashSet<>(alleMitgliedschaften);

        return new Tripple<>(alleAbgeordnetenFinal, alleFraktionenFinal, alleMitgliedschaftenFinal);
    }

    /**
     * Liest alle gehaltenen Reden der Sitzung ein
     *
     * @param sitzung die einzulesende Sitzung als File
     * @return alle gehaltenen Reden in der Sitzung
     */
    @Override
    public Plenarprotokoll parseSitzung(File sitzung)
            throws ParserConfigurationException, IOException, SAXException, ParseException {

        System.out.println(StringHelper.getCurrDateTimeFormatted() + " Parsing " + sitzung.getName() + " ...");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(sitzung);

        // Eckdaten der Sitzung einlesen

        Element datumNode = (Element) document.getElementsByTagName("datum").item(0);
        Date datum = StringHelper.toDate(datumNode.getAttribute("date"));
        Date beginn = StringHelper.toDateTime(datumNode.getAttribute("date"), ((Element) document.getElementsByTagName("sitzungsbeginn").item(0)).getAttribute("sitzung-start-uhrzeit"));
        Date ende = StringHelper.toDateTime(datumNode.getAttribute("date"), ((Element) document.getElementsByTagName("sitzungsende").item(0)).getAttribute("sitzung-ende-uhrzeit"));
        assert ende != null;

        // Für den Fall, dass die Sitzung über Mitternacht gehen sollte:
        if (ende.before(beginn)) {
            ende = new Date(ende.getTime() + 86400000); // Ein Tag hat 86400000 Millisekunden
        }

        int sitzungsnummer = StringHelper.toNaturalNumber(document.getElementsByTagName("sitzungsnr").item(1).getTextContent());
        Wahlperiode wahlperiode = this.wahlperioden.stream().filter(o -> o.getNumber() == Integer.parseInt(document.getElementsByTagName("wahlperiode").item(0).getTextContent())).findFirst().get();
        String ort = document.getElementsByTagName("ort").item(0).getTextContent();

        // Jeden Tagesordnungspunkt der Sitzung mit den darin enthaltenen Reden einlesen

        List<Rede> alleReden = new ArrayList<>();

        NodeList tagesordnungspunkte = document.getElementsByTagName("tagesordnungspunkt");

        for (int t = 0; t < tagesordnungspunkte.getLength(); t++) {
            Element tagesordnungspunkt = (Element) tagesordnungspunkte.item(t);
            NodeList reden = tagesordnungspunkt.getElementsByTagName("rede");

            for (int i = 0; i < reden.getLength(); i++) {
                Element rede = (Element) reden.item(i); // i-te Rede des Tagesordnungspunktes

                // ID der Rede einlesen
                int ID = StringHelper.toNaturalNumber(rede.getAttribute("id").substring(2));

                // Redner erfassen
                Element redner = (Element) rede.getElementsByTagName("redner").item(0);
                int rednerID = StringHelper.toNaturalNumber(redner.getAttribute("id"));

                Abgeordneter abgeordneter;
                try {
                    abgeordneter = getAbgeordneterByID(rednerID);
                } catch (AbgeordneterNotFoundException e) {
                    continue; // Falls kein Abgeordneter die Rede hält, ist sie irrelevant
                }

                // Redetext ohne Kommentare einlesen
                StringBuilder redeText = new StringBuilder();

                boolean rednerRedet = true;

                NodeList elements = rede.getChildNodes();
                for (int j = 0; j < elements.getLength(); j++) {
                    if (elements.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) elements.item(j); // j-ter "Teil" der Rede

                        // Kommentare zählen nicht zur Rede und werden daher ignoriert
                        if (element.getTagName().equals("kommentar")) {
                            continue;
                        }
                        // Nach einem "name"-Tag redet i.d.R. der (Vize)-Präsident o.Ä., nicht der Redner
                        // → folgenden Text nicht mit zur Rede nehmen, bis der Redner selbst wieder dran ist
                        else if (element.getTagName().equals("name")) {
                            rednerRedet = false;
                        }
                        // Zwischenfragen (wenn ein Redner zwischen der Rede aufgeführt wird) zählen nicht zu der Rede dazu
                        // - Es sei denn, das Redner-Attribut ist der Redner selbst → dann wird die Rede fortgeführt:
                        else if (element.getAttribute("klasse").equals("redner")) {
                            int zwischenrednerID = StringHelper.toNaturalNumber(element.getElementsByTagName("redner").item(0).getAttributes().item(0).getTextContent());
                            if (zwischenrednerID == rednerID) {
                                rednerRedet = true;
                                continue;
                            }
                            rednerRedet = false;
                        }
                        // Sonst p-Knoten, der Text enthält, einlesen
                        else if (rednerRedet) {
                            String neuerText = element.getTextContent();
                            redeText.append(" ").append(neuerText);
                        }
                    }
                }

                Rede r = new RedeImpl(ID, "ID" + ID, abgeordneter, redeText.toString(), datum);
                alleReden.add(r);
                abgeordneter.addRede(r);
                this.reden.add(r);
            }
        }
        return new PlenarprotokollImpl("Plenarprotokoll - Sitzung " + sitzungsnummer, wahlperiode, ort, sitzungsnummer, datum, beginn, ende, alleReden);
    }

    /**
     * @return Gibt eine Liste aller Abgeordneten zurück
     */
    @Override
    public Set<Abgeordneter> listAbgeordnete() {
        return this.abgeordnete;
    }

    /**
     * @return Gibt eine Liste aller Fraktionen zurück
     */
    @Override
    public Set<Fraktion> listFraktionen() {
        return this.fraktionen;
    }

    /**
     * @return alle Fraktionen aus der 20. Wahlperiode mit Rednern im Bundestag
     */
    public List<String> listFraktionenWithRedenWP20() {

        // Datenbankabfrage
        List<Bson> query = Arrays.asList(
                Aggregates.unwind("$reden", new UnwindOptions().preserveNullAndEmptyArrays(false)),
                Aggregates.unwind("$wahlperioden.WP20.fraktionen"),
                Aggregates.project(Projections.fields(
                        Projections.include("wahlperioden.WP20.fraktionen"))),
                Aggregates.group("$wahlperioden.WP20.fraktionen")
        );

        AggregateIterable<org.bson.Document> fraktionenWP20 = mongoDB.aggregate(query, "Abgeordnete");

        List<String> result = new ArrayList<>();

        for (org.bson.Document doc : fraktionenWP20) {
            result.add(doc.getString("_id"));
        }

        return result;
    }

    /**
     * @return Gibt eine Liste aller erfassten Parteinamen zurück
     */
    @Override
    public Set<Partei> listParteien() {
        return this.parteien;
    }

    /**
     * @return Gibt eine Liste aller Mitgliedschaften zurück
     */
    @Override
    public Set<Mitgliedschaft> listMitgliedschaften() {
        return this.mitgliedschaften;
    }

    /**
     * @return Gibt eine Liste aller erfassten Wahlperioden zurück
     */
    @Override
    public List<Wahlperiode> getWahlperioden() {
        return this.wahlperioden;
    }

    /**
     * @return alle eingelesenen Reden
     */
    @Override
    public List<Rede> listReden() {
        return this.reden;
    }

    /**
     * @return listet alle eingelesenen Sitzungen auf
     */
    @Override
    public List<Plenarprotokoll> listPlenarprotokolle() {
        return this.plenarprotokolle;
    }

    /**
     * Gibt einen Abgeordneten nach ID zurück
     *
     * @param ID die ID des zu findenden Abgeordneten
     * @return der Abgeordnete mit der übergebenen ID
     * @throws AbgeordneterNotFoundException falls der Abgeordnete mit der angegebenen ID nicht existiert
     */
    @Override
    public AbgeordneterImpl getAbgeordneterByID(int ID) throws AbgeordneterNotFoundException {
        for (Abgeordneter abgeordneter : this.abgeordnete) {
            if (abgeordneter.getID().equals(ID)) {
                return (AbgeordneterImpl) abgeordneter;
            }
        }
        throw new AbgeordneterNotFoundException("Es existiert kein Abgeordneter mit der ID " + ID);
    }

    /**
     * Gibt einen Abgeordneten nach ID zurück
     *
     * @param ID die ID des zu findenden Abgeordneten
     * @return der Abgeordnete mit der übergebenen ID
     * @throws AbgeordneterNotFoundException falls der Abgeordnete mit der angegebenen ID nicht existiert
     */
    @Override
    public Abgeordneter getAbgeordneterByIDFromMongoDB(int ID) throws AbgeordneterNotFoundException {
        for (Abgeordneter abgeordneter : getAbgeordneteDB()) {
            if (abgeordneter.getID().equals(ID)) {
                return abgeordneter;
            }
        }
        throw new AbgeordneterNotFoundException("Es existiert kein Abgeordneter mit der ID " + ID);
    }

    /**
     * Gibt einen Abgeordneten nach Vor-Nachname und Wahlperiode zurück
     *
     * @param wahlperiode eine beliebige Wahlperiode, in der der Abgeordnete ein Mandat hatte
     * @param nachname    der Nachname des zu zurückzugebenden Abgeordneten
     * @param vorname     der Vorname des zu zurückzugebenden Abgeordneten
     * @return der (hoffentlich) passende Abgeordnete
     */
    @Override
    public AbgeordneterImpl getAbgeordneterByWahlperiodeAndName(int wahlperiode, String nachname, String vorname) throws AbgeordneterNotFoundException {
        // Wahlperiode bestimmen
        WahlperiodeImpl wp = (WahlperiodeImpl) this.wahlperioden.stream().filter(w -> w.getNumber() == wahlperiode).findFirst().orElse(null);

        // Die Abgeordneten über die Mandate der Wahlperiode finden
        assert wp != null;
        for (Mandat m : wp.listMandate()) {
            AbgeordneterImpl abgeordneter = (AbgeordneterImpl) m.getAbgeordneter();

            if (StringHelper.simplify(abgeordneter.getName()).equals(StringHelper.simplify(nachname))
                    && StringHelper.simplify(abgeordneter.getVorname()).equals(StringHelper.simplify(vorname))) {
                return abgeordneter;
            }
        }
        throw new AbgeordneterNotFoundException();
    }

    /**
     * Gibt einen Abgeordneten nach Vor- und Nachname zurück, sofern in der Datenbank vorhanden
     *
     * @param nachname der Nachname des zu zurückzugebenden Abgeordneten
     * @param vorname  der Vorname des zu zurückzugebenden Abgeordneten
     * @return der (hoffentlich) passende Abgeordnete
     */
    @Override
    public Abgeordneter getAbgeordneterByNameFromMongoDB(String nachname, String vorname) throws BundestagException {

        for (Abgeordneter abgeordneter : getAbgeordneteDB()) {
            if (StringHelper.simplify(abgeordneter.getName()).equals(StringHelper.simplify(nachname))
                    && StringHelper.simplify(abgeordneter.getVorname()).equals(StringHelper.simplify(vorname))) {
                return abgeordneter;
            }
        }
        throw new AbgeordneterNotFoundException();
    }

    /**
     * @return die Abgeordneten, die aus der MongoDB eingelesen wurden
     */
    public List<AbgeordneterMongoDBImpl> getAbgeordneteDB() {
        return this.abgeordneteDB;
    }

    /**
     * @return die Reden, die aus der MongoDB eingelesen wurden
     */
    public Map<String, RedeMongoDBImpl> getRedenDB() {
        return this.redenDB;
    }

    /**
     * @param ID die ID der zu findenden Rede
     * @return die Rede mit der übergebenen ID
     */
    @Override
    public Rede getRedeByIDFromMongoDB(int ID) throws RedeNotFoundException {

        RedeMongoDBImpl result = this.getRedenDB().get(String.valueOf(ID));

        if (result == null) {
            throw new RedeNotFoundException("Die Rede mit der ID " + ID + " existiert nicht.");
        }

        return result;
    }
}
