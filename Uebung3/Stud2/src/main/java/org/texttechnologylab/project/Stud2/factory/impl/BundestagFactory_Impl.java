package org.texttechnologylab.project.Stud2.factory.impl;


import org.texttechnologylab.project.Stud2.data.Abgeordneter;
import org.texttechnologylab.project.Stud2.data.Ausschuss;
import org.texttechnologylab.project.Stud2.data.Fraktion;
import org.texttechnologylab.project.Stud2.data.Gruppe;
import org.texttechnologylab.project.Stud2.data.Kommentar;
import org.texttechnologylab.project.Stud2.data.Mandat;
import org.texttechnologylab.project.Stud2.data.Mitgliedschaft;
import org.texttechnologylab.project.Stud2.data.Partei;
import org.texttechnologylab.project.Stud2.data.Rede;
import org.texttechnologylab.project.Stud2.data.Sitzung;
import org.texttechnologylab.project.Stud2.data.Tagesordnung;
import org.texttechnologylab.project.Stud2.data.Tagesordnungspunkt;
import org.texttechnologylab.project.Stud2.data.Types;
import org.texttechnologylab.project.Stud2.data.Wahlkreis;
import org.texttechnologylab.project.Stud2.data.Wahlperiode;
import org.texttechnologylab.project.Stud2.data.impl.Abgeordneter_File_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Ausschuss_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Fraktion_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Gruppe_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Kommentar_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Mandat_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Mitgliedschaft_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Partei_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Rede_File_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Sitzung_File_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Tagesordnung_File_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Tagesordnungspunkt_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Wahlkreis_Impl;
import org.texttechnologylab.project.Stud2.data.impl.Wahlperiode_Impl;
import org.texttechnologylab.project.Stud2.exception.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud2.exception.BundestagException;
import org.texttechnologylab.project.Stud2.factory.BundestagFactory;
import org.texttechnologylab.project.Stud2.utils.StringFunctions;
import org.texttechnologylab.project.Stud2.utils.Tripple;
import org.texttechnologylab.project.Stud2.utils.Tuple;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Klasse für eine BundestagFactory, über die alle Bundestag-Objekte erzeugt werden sollen
 *
 * @author Stud2
 */
public class BundestagFactory_Impl implements BundestagFactory {
    private final Set<Abgeordneter> abgeordnete;
    private final Set<Fraktion> fraktionen;
    private Set<Partei> parteien = new HashSet<>();
    private final Set<Mitgliedschaft> mitgliedschaften;
    private List<Wahlperiode> wahlperioden = new ArrayList<>();
    private List<Sitzung> sitzungen;
    private List<Rede> reden = new ArrayList<>();
    private List<Tagesordnung> tagesordnungen = new ArrayList<>();
    private List<Kommentar> kommentare = new ArrayList<>();

    /**
     * Konstruktor für eine BundestagFactory
     *
     * @param pathMDB_Stammdaten der Pfad zur einzulesenden MDB_STAMMDATEN.XML-Datei
     * @param pathBundestagsreden20 der Pfad zum ORDNER mit den XML-Sitzungsprotokollen
     */
    public BundestagFactory_Impl(String pathMDB_Stammdaten, String pathBundestagsreden20)
            throws IOException, ParserConfigurationException, SAXException, ParseException, BundestagException {

        // MDB_Stammdaten.XML parsen
        Tripple<Set<Abgeordneter>, Set<Fraktion>, Set<Mitgliedschaft>> parseResult = parseAbgeordnete(pathMDB_Stammdaten);
        System.out.println("\n ** Parsing of MdB-Stammdaten completed. **\n");

        this.abgeordnete = parseResult.getFirst();
        this.fraktionen = parseResult.getSecond();
        this.mitgliedschaften = parseResult.getThird();

        // Ordner Bundestagsreden20 mit den XML-Sitzungsprotokollen parsen

        List<Sitzung> alleSitzungen = new ArrayList<>();

        // Ist der Pfad leer, so wird keine Rede eingelesen
        if (!pathBundestagsreden20.isEmpty()) {
            File[] sitzungen = new File(pathBundestagsreden20).listFiles();
            assert sitzungen != null;

            List<Tuple<File, Integer>> filesSorted = new ArrayList<>();

            for (File sitzung : sitzungen) {
                if (sitzung.getName().endsWith(".xml")) {
                    filesSorted.add(new Tuple<>(sitzung, Integer.parseInt(sitzung.getName().split("\\.")[0])));}
            }

            filesSorted.sort(Comparator.comparing(Tuple::getSecond));

            for (Tuple<File, Integer> sitzung : filesSorted) {
                if (sitzung.getFirst().getName().endsWith(".xml")) {
                    alleSitzungen.add(parseSitzung(sitzung.getFirst()));
                }
            }
            System.out.println("\n ** Parsing of Bundestagsreden20 completed. **\n");
        }
        this.sitzungen = alleSitzungen;
    }

    /**
     * Liest MDB_STAMMDATEN.XML ein
     *
     * @param path Der Pfad zur einzulesenden MDB_STAMMDATEN.XML-Datei
     * @return Ein 3-Tupel der Form (alle Abgeordneten, alle Fraktionen, alle Mitgliedschaften)
     */
    @Override
    public Tripple<Set<Abgeordneter>, Set<Fraktion>, Set<Mitgliedschaft>> parseAbgeordnete(String path)
            throws ParserConfigurationException, IOException, SAXException, ParseException, BundestagException {

        System.out.println("\nParsing MDB_STAMMDATEN.XML ...");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // Liste aller Abgeordneten
        List<Abgeordneter> alleAbgeordneten = new ArrayList<>();

        // Liste aller Fraktionen
        List<Fraktion> alleFraktionen = new ArrayList<>();
        alleFraktionen.add(new Fraktion_Impl("Fraktionslos"));
        alleFraktionen.add(new Fraktion_Impl("Fraktion BÜNDNIS 90/DIE GRÜNEN"));

        // Alle Mitgliedschaften inklusive Fraktionen, Ausschüsse und sonstigen Gruppierungen
        Set<Mitgliedschaft> alleMitgliedschaften = new HashSet<>();

        // Liste aller Parteien
        List<Partei> alleParteien = new ArrayList<>();
        alleParteien.add(new Partei_Impl("Plos"));
        alleParteien.add(new Partei_Impl("BÜNDNIS 90/DIE GRÜNEN"));

        // Alle Ausschüsse
        Set<Ausschuss> alleAusschuesse = new HashSet<>();

        // Alle sonstigen Gruppen, d.h. ohne Parteien, Fraktionen und Ausschüssen
        Set<Gruppe> sonstGruppen = new HashSet<>();

        // Alle Wahlperioden
        List<Wahlperiode> alleWahlperioden = new ArrayList<>();

        // Alle Wahlkreise
        List<Wahlkreis> alleWahlkreise = new ArrayList<>();
        alleWahlkreise.add(new Wahlkreis_Impl("k. A.", -1));

        // MDB_STAMMDATEN.XML parsen

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document mdbStammdaten = db.parse(new File(path));

        NodeList mdbs = mdbStammdaten.getElementsByTagName("MDB"); // Enthält alle Abgeordneten-Knoten

        for (int i=0; i < mdbs.getLength(); i++) {
            Element mdb = (Element) mdbs.item(i); // i-ter Abgeordneter des Bundestags

            // Details zum NAMEN eines Abgeordneten einlesen

            Element namen = (Element) mdb.getElementsByTagName("NAMEN").item(0);
            int sizeNamen = namen.getElementsByTagName("NAME").getLength();
            // Wir nehmen nur den letzten Eintrag mit dem Tag "NAME", da dieser der aktuellste ist
            Element name = (Element) namen.getElementsByTagName("NAME").item(sizeNamen - 1);

            int mdbID = StringFunctions.toInt(mdb.getElementsByTagName("ID").item(0).getTextContent());
            String nachname = name.getElementsByTagName("NACHNAME").item(0).getTextContent();
            String vorname = name.getElementsByTagName("VORNAME").item(0).getTextContent();
            String ortszusatz = name.getElementsByTagName("ORTSZUSATZ").item(0).getTextContent();
            String adel = name.getElementsByTagName("ADEL").item(0).getTextContent();
            String anredeTitel = name.getElementsByTagName("ANREDE_TITEL").item(0).getTextContent();
            String akadTitel = name.getElementsByTagName("AKAD_TITEL").item(0).getTextContent();

            // BIOGRAFISCHE ANGABEN eines Abgeordneten einlesen

            Element bio = (Element) mdb.getElementsByTagName("BIOGRAFISCHE_ANGABEN").item(0);

            Date geburtsdatum = StringFunctions.toDate(bio.getElementsByTagName("GEBURTSDATUM").item(0).getTextContent());
            String geburtsort = bio.getElementsByTagName("GEBURTSORT").item(0).getTextContent();
            Date sterbedatum = StringFunctions.toDate(bio.getElementsByTagName("STERBEDATUM").item(0).getTextContent());

            String prov_geschlecht = bio.getElementsByTagName("GESCHLECHT").item(0).getTextContent();
            Types.GESCHLECHT geschlecht;
            if (prov_geschlecht.equals("männlich")) {
                geschlecht = Types.GESCHLECHT.MAENNLICH;
            }
            else {
                geschlecht = Types.GESCHLECHT.WEIBLICH;
            }

            String religion = bio.getElementsByTagName("RELIGION").item(0).getTextContent();
            String beruf = bio.getElementsByTagName("BERUF").item(0).getTextContent();
            String vita = bio.getElementsByTagName("VITA_KURZ").item(0).getTextContent();

            String prov_partei = bio.getElementsByTagName("PARTEI_KURZ").item(0).getTextContent();
            Partei partei = alleParteien.get(0); // Per Default ist der Abgeordnete parteilos

            // Neue PARTEI des Abgeordneten initialisieren und in alleParteien eingefügen, WENN noch nicht vorhanden

            boolean partei_exists = false;

            for (Partei p: alleParteien) {
                if (prov_partei.equals("DIE GRÜNEN/BÜNDNIS 90") || p.getLabel().equals(prov_partei)){
                    partei_exists = true;
                    break;
                }
            }

            if (!partei_exists && !prov_partei.isEmpty()) {
                partei = new Partei_Impl(prov_partei);
                alleParteien.add(partei);
            }
            else {
                // Ansonsten passende Partei aus alleParteien heraussuchen und zuweisen, da schon vorhanden
                for (Partei p_i: alleParteien) {
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

            Abgeordneter abgeordneter = new Abgeordneter_File_Impl(mdbID, nachname, vorname, ortszusatz, adel, anredeTitel,
                    akadTitel, geburtsdatum, geburtsort, sterbedatum, geschlecht, religion, beruf, vita, partei);

            alleAbgeordneten.add(abgeordneter);
            partei.addMember(abgeordneter);

            // Alle WAHLPERIODEN des Abgeordneten einlesen, in alleWahlperioden abspeichern und alle darin enthaltenen
            // MANDATE des Abgeordneten erfassen

            Element wahlperioden = (Element) mdb.getElementsByTagName("WAHLPERIODEN").item(0);
            NodeList wpn = wahlperioden.getElementsByTagName("WAHLPERIODE");

            for (int j = 0; j < wpn.getLength(); j++) {
                Element wp = (Element) wpn.item(j); // j-te Wahlperiode des Abgeordneten

                int nummer = StringFunctions.toInt(wp.getElementsByTagName("WP").item(0).getTextContent());
                Date startDate = StringFunctions.toDate(wp.getElementsByTagName("MDBWP_VON").item(0).getTextContent());
                Date endDate = StringFunctions.toDate(wp.getElementsByTagName("MDBWP_BIS").item(0).getTextContent());

                // WAHLPERIODE initialisieren und in alleWahlperioden eingefügen, WENN noch nicht vorhanden

                Wahlperiode wahlperiode = null;

                boolean wahlperiode_exists = false;

                for (Wahlperiode p: alleWahlperioden) {
                    if (p.getNumber() == nummer) {
                        wahlperiode_exists = true;
                        break;
                    }
                }

                if (!wahlperiode_exists) {
                    wahlperiode = new Wahlperiode_Impl("Wahlperiode" + nummer, nummer, startDate, endDate);
                    alleWahlperioden.add(wahlperiode);
                }
                else {
                    // Ansonsten passende Wahlperiode aus alleWahlperioden zuweisen, da schon vorhanden
                    for (Wahlperiode w_i: alleWahlperioden) {
                        if (w_i.getNumber() == nummer) {
                            wahlperiode = w_i;
                            break;
                        }
                    }
                }

                // Neuen WAHLKREIS initialisieren und in alleWahlkreise hinzufügen, WENN nicht vorhanden

                int wahlkreisNummer = StringFunctions.toInt(wp.getElementsByTagName("WKR_NUMMER").item(0).getTextContent());

                Wahlkreis wahlkreis = alleWahlkreise.get(0); // Default: ohne Angabe, Wahlkreisnummer = -1

                boolean wahlkreis_exists = false;

                for (Wahlkreis wk: alleWahlkreise) {
                    if (wk.getNumber() == wahlkreisNummer) {
                        wahlkreis_exists = true;
                        break;
                    }
                }

                // -1 als Wahlkreisnummer bedeutet hier, dass der Eintrag leer war → kein Wahlkreis erzeugen
                if (!wahlkreis_exists && wahlkreisNummer != -1) {
                    wahlkreis = new Wahlkreis_Impl("Wahlkreis " + wahlkreisNummer, wahlkreisNummer);
                    alleWahlkreise.add(wahlkreis);
                }
                else {
                    // Ansonsten passenden Wahlkreis aus alleWahlkreise und zuweisen, da schon vorhanden:
                    for (Wahlkreis w_i: alleWahlkreise) {
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
                    typ = null;
                }

                // MANDAT initialisieren und zum Set aller Mandate dieses Abgeordneten hinzufügen
                // Achtung: Fraktionen, Ausschüsse und sonstige Mitgliedschaften werden im Nachhinein dem Objekt hinzugefügt

                Mandat mandat = new Mandat_Impl("MANDAT-" + abgeordneter.getLabel() + "-WP" + nummer,
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
                    Date mitgliedVon = StringFunctions.toDate(inst.getElementsByTagName("MDBINS_VON").item(0).getTextContent());
                    Date mitgliedBis = StringFunctions.toDate(inst.getElementsByTagName("MDBINS_BIS").item(0).getTextContent());

                    // Alle MITGLIEDSCHAFTEN des Abgeordneten erfassen

                    Mitgliedschaft mitgliedschaft = null;

                    switch (insArt) {
                        case "Ausschuss":
                        case "Unterausschuss":
                        case "Untersuchungsausschuss":
                            Ausschuss ausschuss = null;

                            // Neuen AUSSCHUSS initialisieren, in alleAusschuesse sowie in das Mandat einfügen, WENN noch nicht vorhanden

                            boolean ausschuss_exists = false;

                            for (Ausschuss a: alleAusschuesse) {
                                if (insLang.equals(a.getLabel())){
                                    ausschuss_exists = true;
                                    break;
                                }
                            }

                            if (!ausschuss_exists) {
                                ausschuss = new Ausschuss_Impl(insLang, insArt);
                                alleAusschuesse.add(ausschuss);
                            }
                            else {
                                // Ansonsten passenden Ausschuss aus alleAusschuesse zuweisen, da schon vorhanden:
                                for (Ausschuss a_i: alleAusschuesse) {
                                    if (a_i.getLabel().equals(insLang)) {
                                        ausschuss = a_i;
                                        break;
                                    }
                                }
                            }
                            mitgliedschaft = new Mitgliedschaft_Impl(abgeordneter, funktion, mitgliedVon, mitgliedBis, ausschuss, wahlperiode);
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

                            for (Gruppe a: sonstGruppen) {
                                if (insLang.equals(a.getLabel())){
                                    gruppe_exists = true;
                                    break;
                                }
                            }

                            if (!gruppe_exists) {
                                gruppe = new Gruppe_Impl(insLang);
                                sonstGruppen.add(gruppe);
                            }
                            else {
                                // Ansonsten passende Gruppe aus sonstGruppen zuweisen, da schon vorhanden:
                                for (Gruppe a_i: sonstGruppen) {
                                    if (a_i.getLabel().equals(insLang)) {
                                        gruppe = a_i;
                                        break;
                                    }
                                }
                            }
                            mitgliedschaft = new Mitgliedschaft_Impl(abgeordneter, funktion, mitgliedVon, mitgliedBis, gruppe, wahlperiode);

                            if (gruppe != null) {
                                gruppe.addMember(abgeordneter);
                            }
                            break;

                        case "Fraktion/Gruppe":
                            Fraktion fraktion = alleFraktionen.get(0); // Per Default ist der Abgeordnete fraktionslos

                            // Neue FRAKTION initialisieren und in alleFraktionen einfügen, WENN noch nicht vorhanden

                            boolean fraktion_exists = false;

                            for (Fraktion a: alleFraktionen) {
                                if (insLang.equals(a.getLabel()) || insLang.equals("Fraktion Die Grünen/Bündnis 90")){
                                    fraktion_exists = true;
                                    break;
                                }
                            }

                            if (!fraktion_exists) {
                                fraktion = new Fraktion_Impl(insLang);
                                alleFraktionen.add(fraktion);
                            }
                            else {
                                // Ansonsten passende Fraktion aus alleFraktionen heraussuchen, da schon vorhanden:
                                for (Fraktion a_i: alleFraktionen) {
                                    if (a_i.getLabel().equals(insLang)
                                            // Wir nehmen an, dass die folgenden beiden Fraktionen äquivalent sind
                                            || (a_i.getLabel().equals("Fraktion BÜNDNIS 90/DIE GRÜNEN")
                                            && insLang.equals("Fraktion Die Grünen/Bündnis 90"))) {
                                        fraktion = a_i;
                                        break;
                                    }
                                }
                            }
                            mitgliedschaft = new Mitgliedschaft_Impl(abgeordneter, funktion, mitgliedVon, mitgliedBis, fraktion, wahlperiode);
                            mandat.addFraktion(fraktion);
                            fraktion.addMember(abgeordneter); // Abgeordneten und seine Funktion zur Fraktion hinzufügen

                            if (!funktion.equals("k. A.") && wahlperiode != null) {
                                fraktion.addFunktionstraeger(funktion + " (WP" + wahlperiode.getNumber() + ")" , abgeordneter);}
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
                Mitgliedschaft_Impl mitgliedPartei = new Mitgliedschaft_Impl(abgeordneter, "Mitglied in WP" +
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
    public Sitzung parseSitzung(File sitzung)
            throws ParserConfigurationException, IOException, SAXException, BundestagException, ParseException {

        System.out.println("Parsing " + sitzung.getName() + " ...");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(sitzung);

        // Eckdaten der Sitzung einlesen
        Element datumNode = (Element) document.getElementsByTagName("datum").item(0);
        Date datum = StringFunctions.toDate(datumNode.getAttribute("date"));
        Date beginn = StringFunctions.toDateTime(datumNode.getAttribute("date"), ((Element) document.getElementsByTagName("sitzungsbeginn").item(0)).getAttribute("sitzung-start-uhrzeit"));
        Date ende = StringFunctions.toDateTime(datumNode.getAttribute("date"), ((Element) document.getElementsByTagName("sitzungsende").item(0)).getAttribute("sitzung-ende-uhrzeit"));
        assert ende != null;

        // Für den Fall, dass die Sitzung über Mitternacht gehen sollte:
        if (ende.before(beginn)) {
            ende = new Date(ende.getTime() + 86400000); // Ein Tag hat 86400000 Millisekunden
        }

        int sitzungsnummer = StringFunctions.toInt(document.getElementsByTagName("sitzungsnr").item(1).getTextContent());
        Wahlperiode wahlperiode = this.wahlperioden.stream().filter(o -> o.getNumber() == Integer.parseInt(document.getElementsByTagName("wahlperiode").item(0).getTextContent())).findFirst().get();
        String ort = document.getElementsByTagName("ort").item(0).getTextContent();

        Sitzung s = new Sitzung_File_Impl("Sitzung " + sitzungsnummer, wahlperiode, ort, sitzungsnummer, datum, beginn, ende);

        // Jeden Tagesordnungspunkt der Sitzung mit den darin enthaltenen Reden einlesen
        List<Tagesordnungspunkt> alleTagesordnungspunkte = new ArrayList<>();

        NodeList tagesordnungspunkte = document.getElementsByTagName("tagesordnungspunkt");

        for (int t=0; t < tagesordnungspunkte.getLength(); t++) {
            Element tagesordnungspunkt = (Element) tagesordnungspunkte.item(t);

            String label = tagesordnungspunkt.getAttribute("top-id").replace(" ", " ");
            NodeList reden = tagesordnungspunkt.getElementsByTagName("rede");

            List<Rede> alleReden = new ArrayList<>();

            for (int i=0; i < reden.getLength(); i++) {
                Element rede = (Element) reden.item(i); // i-te Rede des Tagesordnungspunktes

                // ID der Rede einlesen
                int ID = StringFunctions.toInt(rede.getAttribute("id").substring(2));

                // Redner erfassen
                Element redner = (Element) rede.getElementsByTagName("redner").item(0);
                int rednerID = StringFunctions.toInt(redner.getAttribute("id"));

                Abgeordneter abgeordneter;
                try {
                    abgeordneter = getAbgeordneterByID(rednerID);
                }
                catch (AbgeordneterNotFoundException e) {
                    continue; // Falls kein Abgeordneter die Rede hält, ist sie irrelevant
                }

                // Redetext und Kommentare einlesen
                StringBuilder redeText = new StringBuilder();
                List<Kommentar> kommentareRede = new ArrayList<>();

                boolean rednerRedet = true;

                NodeList elements = rede.getChildNodes();
                for (int j = 0; j < elements.getLength(); j++) {
                    if (elements.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) elements.item(j); // j-ter "Teil" der Rede

                        // Kommentare zählen nicht zur Rede - dafür erzeugen wir ein eigenes Objekt und ordnen es dem Abgeordneten sowie der Rede zu
                        if (element.getTagName().equals("kommentar")) {
                            Kommentar kommentar = parseKommentar(element.getTextContent(), ID);
                            if (!(kommentar == null)) {
                                this.kommentare.add(kommentar);
                                kommentareRede.add(kommentar);
                                abgeordneter.addKommentar(kommentar);
                            }
                        }
                        // Nach einem "name"-Tag redet i.d.R. der (Vize)-Präsident o.Ä., nicht der Redner
                        // → folgenden Text nicht mit zur Rede nehmen, bis der Redner selbst wieder dran ist
                        else if (element.getTagName().equals("name")) {
                            rednerRedet = false;
                        }
                        // Zwischenfragen (wenn ein Redner zwischen der Rede aufgeführt wird) zählen nicht zu der Rede dazu
                        // - Es sei denn, das Redner-Attribut ist der Redner selbst → dann wird die Rede fortgeführt:
                        else if (element.getAttribute("klasse").equals("redner")) {
                            int zwischenrednerID = StringFunctions.toInt(element.getElementsByTagName("redner").item(0).getAttributes().item(0).getTextContent());
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

                Rede r = new Rede_File_Impl(ID, "ID" + ID, abgeordneter, redeText.toString(), datum, kommentareRede);
                alleReden.add(r);
                abgeordneter.addRede(r);
                this.reden.add(r);
            }

            alleTagesordnungspunkte.add(new Tagesordnungspunkt_Impl(label, alleReden));
        }

        // Themen der Tagesordnungspunkte aus dem Inhaltsverzeichnis extrahieren
        List<Tuple<String, String>> tagesordnungspunktInhalt = new ArrayList<>();
        NodeList ivzBloecke = document.getElementsByTagName("ivz-block");

        for (int i = 0; i < ivzBloecke.getLength(); i++) {
            Element ivzBlock = (Element) ivzBloecke.item(i);

            if (!(ivzBlock.getElementsByTagName("ivz-block-titel").getLength() == 0)) {
                String blockTitel = ivzBlock.getElementsByTagName("ivz-block-titel").item(0).getTextContent();

                if (blockTitel.contains("Tagesordnungspunkt") || blockTitel.contains("Zusatzpunkt")) {

                    // SchönheitOP für as Thema
                    String thema = ivzBlock.getElementsByTagName("ivz-eintrag-inhalt").item(0).getTextContent();
                    thema = thema.trim().replace("\n", " ").replaceAll(" +", " ");

                    tagesordnungspunktInhalt.add(new Tuple<>(blockTitel, thema));
                }
            }
        }

        // Themen den entsprechenden Tagesordnungspunkten zuweisen
        for (Tagesordnungspunkt tagesordnungspunkt : alleTagesordnungspunkte) {
            for (Tuple<String, String> inhalte : tagesordnungspunktInhalt) {
                if (inhalte.getFirst().contains(tagesordnungspunkt.getLabel())    &&
                    !tagesordnungspunkt.getLabel().contains("Einzelplan")         &&
                    !tagesordnungspunkt.getLabel().contains("Geschäftsordnung")) {

                    tagesordnungspunkt.setAngelegenheit(inhalte.getSecond());
                    break;
                }
            }
            if (tagesordnungspunkt.getAngelegenheit() == null) {
                tagesordnungspunkt.setAngelegenheit("k. A.");
            }
        }

        Tagesordnung tagesordnung = new Tagesordnung_File_Impl("Tagesordnung der " + sitzungsnummer + ". Sitzung", s, alleTagesordnungspunkte);
        s.setTagesordnung(tagesordnung);
        this.tagesordnungen.add(tagesordnung);

        return s;
    }

    /**
     * @param kommentar der String, der im Kommentar-Tag eingespeichert ist
     * @return ein Kommentar-Objekt
     */
    @Override
    public Kommentar parseKommentar(String kommentar, int redeID) throws BundestagException {
        if (kommentar.matches(".*\\[.*\\]:.*")) {

            // Inhalt des Kommentars auslesen
            String text = kommentar.split(":")[1].trim();
            if (text.contains(")")) {
                text = text.substring(0, text.indexOf(")")).trim();
            }
            else if (text.contains("]") && text.contains("-")) {
                text = text.substring(0, text.indexOf("-")).trim();
            }

            // Abgeordneten identifizieren
            String beschreibung = null;

            if (kommentar.indexOf("[") == 1) {
                beschreibung = kommentar.substring(2, kommentar.substring(2).indexOf("[")).trim();
            }
            else if (kommentar.indexOf("[") == 4) {
                beschreibung = kommentar.substring(5, kommentar.substring(5).indexOf("[")).trim();
            }
            else if (kommentar.indexOf("(") == 0){
                beschreibung = kommentar.substring(1, kommentar.indexOf("[")).trim();
            }
            else {
                beschreibung = kommentar.substring(0, kommentar.indexOf("[")).trim();
            }

            String[] beschreibungSplitted = beschreibung.split(" ");

            String nachname = null;
            String vorname = null;

            nachname = beschreibungSplitted[beschreibungSplitted.length - 1];
            if ((beschreibungSplitted[beschreibungSplitted.length - 2].contains(".") && !beschreibungSplitted[beschreibungSplitted.length - 2].contains("Dr")) ||
                beschreibungSplitted[beschreibungSplitted.length - 2].equals("von")) {
                vorname = beschreibungSplitted[beschreibungSplitted.length - 3];
            }
            else {
                vorname = beschreibungSplitted[beschreibungSplitted.length - 2];
            }

            Abgeordneter abgeordneter;
            try {
                abgeordneter = getAbgeordneterByName(nachname, vorname);
            }
            catch (AbgeordneterNotFoundException e) {
                return null;
            }

            return new Kommentar_Impl("Kommentar (Rede:" + redeID + ") von " + abgeordneter.getID(), redeID, (int) abgeordneter.getID(), text);
        }
        return null;
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
     * @return alle eingelesenen Tagesordnungen
     */
    @Override
    public List<Tagesordnung> listTagesordnungen() {
        return this.tagesordnungen;
    }

    /**
     * @return listet alle eingelesenen Sitzungen auf
     */
    @Override
    public List<Sitzung> listSitzungen() {
        return this.sitzungen;
    }

    /**
     * @return alle eingelesenen Kommentare
     */
    @Override
    public List<Kommentar> listKommentare() {
        return this.kommentare;
    }

    /**
     * Gibt einen Abgeordneten nach ID zurück
     *
     * @param ID die ID des zu findenden Abgeordneten
     * @return der Abgeordnete mit der übergebenen ID
     * @throws AbgeordneterNotFoundException falls der Abgeordnete mit der angegebenen ID nicht existiert
     */
    @Override
    public Abgeordneter_File_Impl getAbgeordneterByID(int ID) throws AbgeordneterNotFoundException {
        for (Abgeordneter abgeordneter : this.abgeordnete) {
            if (abgeordneter.getID().equals(ID)) {
                return (Abgeordneter_File_Impl) abgeordneter;
            }
        }
        throw new AbgeordneterNotFoundException("Es existiert kein Abgeordneter mit der ID " + ID);
    }

    /**
     * Gibt einen Abgeordneten nach Vor-Nachname und Wahlperiode zurück
     *
     * @param wahlperiode eine beliebige Wahlperiode, in der der Abgeordnete ein Mandat hatte
     * @param nachname der Nachname des zu zurückzugebenden Abgeordneten
     * @param vorname der Vorname des zu zurückzugebenden Abgeordneten
     * @return der (hoffentlich) passende Abgeordnete
     */
    @Override
    public Abgeordneter_File_Impl getAbgeordneterByWahlperiodeAndName(int wahlperiode, String nachname, String vorname) throws AbgeordneterNotFoundException {
        // Wahlperiode bestimmen
        Wahlperiode_Impl wp = (Wahlperiode_Impl) this.wahlperioden.stream().filter(w -> w.getNumber() == wahlperiode).findFirst().orElse(null);

        // Die Abgeordneten über die Mandate der Wahlperiode finden
        assert wp != null;
        for (Mandat m : wp.listMandate()) {
            Abgeordneter_File_Impl abgeordneter = (Abgeordneter_File_Impl) m.getAbgeordneter();

            if (StringFunctions.simplify(abgeordneter.getName()).equals(StringFunctions.simplify(nachname))
                    && StringFunctions.simplify(abgeordneter.getVorname()).equals(StringFunctions.simplify(vorname))) {
                return abgeordneter;
            }
        }
        throw new AbgeordneterNotFoundException();
    }

    /**
     * Gibt einen Abgeordneten nach Vor- und Nachname zurück
     *
     * @param nachname der Nachname des zu zurückzugebenden Abgeordneten
     * @param vorname der Vorname des zu zurückzugebenden Abgeordneten
     * @return der (hoffentlich) passende Abgeordnete
     */
    @Override
    public Abgeordneter_File_Impl getAbgeordneterByName(String nachname, String vorname) throws BundestagException {

        for (Abgeordneter abgeordneter : this.abgeordnete) {
            if (StringFunctions.simplify(abgeordneter.getName()).equals(StringFunctions.simplify(nachname))
                    && StringFunctions.simplify(abgeordneter.getVorname()).equals(StringFunctions.simplify(vorname))) {
                return (Abgeordneter_File_Impl) abgeordneter;
            }
        }
        throw new AbgeordneterNotFoundException();
    }
}
