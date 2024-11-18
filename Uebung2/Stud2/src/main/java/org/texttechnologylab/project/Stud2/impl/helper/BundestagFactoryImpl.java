package org.texttechnologylab.project.Stud2.impl.helper;

import jxl.read.biff.BiffException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Fraktion;
import org.texttechnologylab.project.data.Mitgliedschaft;
import org.texttechnologylab.project.data.Rede;
import org.texttechnologylab.project.data.Types;
import org.texttechnologylab.project.data.Wahlperiode;
import org.texttechnologylab.project.exception.BundestagException;
import org.texttechnologylab.project.helper.BundestagFactory;

import org.texttechnologylab.project.Stud2.impl.data.AbgeordneterImpl;
import org.texttechnologylab.project.Stud2.impl.data.AbstimmungImpl;
import org.texttechnologylab.project.Stud2.impl.data.AusschussImpl;
import org.texttechnologylab.project.Stud2.impl.data.FraktionImpl;
import org.texttechnologylab.project.Stud2.impl.data.GruppeImpl;
import org.texttechnologylab.project.Stud2.impl.data.MandatImpl;
import org.texttechnologylab.project.Stud2.impl.data.MitgliedschaftImpl;
import org.texttechnologylab.project.Stud2.impl.data.ParteiImpl;
import org.texttechnologylab.project.Stud2.impl.data.RedeImpl;
import org.texttechnologylab.project.Stud2.impl.data.WahlkreisImpl;
import org.texttechnologylab.project.Stud2.impl.data.WahlperiodeImpl;
import org.texttechnologylab.project.Stud2.impl.exception.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud2.impl.exception.RedeNotFoundException;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Klasse für eine BundestagFactory, über die alle Bundestag-Objekte erzeugt werden sollen
 *
 * @author Stud2
 */
public class BundestagFactoryImpl implements BundestagFactory {
    private final Set<Abgeordneter> abgeordnete;
    private final Set<Fraktion> fraktionen;
    private final Set<Mitgliedschaft> mitgliedschaften;
    private final List<Rede> reden;
    private final Set<Tuple<java.sql.Date, Set<Abgeordneter>>> abwesenheiten;
    private List<Wahlperiode> wahlperioden = new ArrayList<>();

    /**
     * Konstruktor für eine BundestagFactory
     *
     * @param pathMDB_Stammdaten der Pfad zur einzulesenden MDB_STAMMDATEN.XML-Datei
     * @param pathBundestagsreden20 der Pfad zum ORDNER mit den XML-Sitzungsprotokollen
     * @param pathAbstimmungen der Pfad zum ORDNER mit den .xls/.xlsx-Dateien mit den Abstimmungsergebnissen
     */
    public BundestagFactoryImpl(String pathMDB_Stammdaten, String pathBundestagsreden20, String pathAbstimmungen)
            throws IOException, ParserConfigurationException, SAXException, ParseException, BundestagException, BiffException, InvalidFormatException {

        // MDB_Stammdaten.XML parsen
        Tripple<Set<Abgeordneter>, Set<Fraktion>, Set<Mitgliedschaft>> parseResult = parseAbgeordnete(pathMDB_Stammdaten);
        System.out.println("\n------------------ Einlesen aller Abgeordneten abgeschlossen.  -------------------");

        this.abgeordnete = parseResult.getFirst();
        this.fraktionen = parseResult.getSecond();
        this.mitgliedschaften = parseResult.getThird();

        // Ordner Bundestagsreden20 mit den XML-Sitzungsprotokollen parsen
        Set<Tuple<java.sql.Date, Set<Abgeordneter>>> abwesenheiten = new HashSet<>();

        List<Rede> alleEingelesenenReden = new ArrayList<>();

        // Ist der Pfad leer, so wird keine Rede eingelesen
        if (!pathBundestagsreden20.isEmpty()) {
            File[] sitzungenFiles = new File(pathBundestagsreden20).listFiles();
            assert sitzungenFiles != null;

            List<File> sitzungenFilesSorted = new ArrayList<>(List.of(sitzungenFiles));
            Collections.sort(sitzungenFilesSorted);

            for (File sitzung : sitzungenFilesSorted) {
                if (sitzung.getName().endsWith(".xml")) {
                    System.out.println("Einlesen von " + sitzung.getName() + " ...");
                    alleEingelesenenReden.addAll(parseSitzung(sitzung));
                    abwesenheiten.add(getEntschuldigteAbgeordnete(sitzung));
                }
            }
            System.out.println("------------------ Einlesen der Bundestagsreden abgeschlossen. -------------------");
        }
        this.abwesenheiten = abwesenheiten;
        this.reden = alleEingelesenenReden;

        // Ordner Abstimmungen mit den einzelnen .xls/.xlsx-Abstimmungen parsen.
        // → Je nach Dateiendung ist leider ein andere Library notwendig:
        // → jxl für Dateiendung .xls oder apache poi für Dateiendung .xlsx

        // Ist der Pfad leer, so wird keine Abstimmung eingelesen
        if (!pathAbstimmungen.isEmpty()) {
            File[] abstimmungenFiles = new File(pathAbstimmungen).listFiles();
            assert abstimmungenFiles != null;

            List<File> xlsxAbstimmungen = new ArrayList<>();
            List<File> xlsAbstimmungen = new ArrayList<>();

            for (File abstimmung : abstimmungenFiles) {
                if (abstimmung.getName().endsWith(".xls")
                        && !abstimmung.getName().equals("15.03.2019: Entwurf eines Gesetzes für mehr Teilhabe im Wahlrecht.xls")
                        && !abstimmung.getName().equals("07.07.2016: Bundeswehreinsatz EUNAVFOR MED Operation SOPHIA.xls")) {
                    xlsAbstimmungen.add(abstimmung);
                }
                else if (abstimmung.getName().endsWith(".xlsx")
                        && !abstimmung.getName().equals("29.10.2020: Bundeswehreinsatz im Irak (Beschlussempfehlung).xlsx")) {
                    xlsxAbstimmungen.add(abstimmung);
                }
            }

            for (File xlsFile : xlsAbstimmungen) {
                System.out.println("Einlesen von " + xlsFile.getName() + " ...");
                parseAbstimmungXLS(xlsFile.getPath());
            }
            System.out.println("\n------------------ Einlesen der xls-Abstimmungen abgeschlossen.  ------------------");
            for (File xlsxFile : xlsxAbstimmungen) {
                System.out.println("Einlesen von " + xlsxFile.getName() + " ...");
                parseAbstimmungXLSX(xlsxFile.getPath());
            }
            System.out.println("\n------------------ Einlesen der xlsx-Abstimmungen abgeschlossen. ------------------");
        }
    }

    /**
     * Liest MDB_STAMMDATEN.XML ein
     *
     * @param path Der Pfad zur einzulesenden MDB_STAMMDATEN.XML-Datei
     * @return Ein 3-Tupel der Form (alle Abgeordneten, alle Fraktionen, alle Mitgliedschaften)
     */
    public Tripple<Set<Abgeordneter>, Set<Fraktion>, Set<Mitgliedschaft>> parseAbgeordnete(String path)
            throws ParserConfigurationException, IOException, SAXException, ParseException, BundestagException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // Liste aller Abgeordneten
        List<AbgeordneterImpl> alleAbgeordneten = new ArrayList<>();

        // Liste aller Fraktionen
        List<FraktionImpl> alleFraktionen = new ArrayList<>();
        alleFraktionen.add(new FraktionImpl("Fraktionslos"));
        alleFraktionen.add(new FraktionImpl("Fraktion BÜNDNIS 90/DIE GRÜNEN"));

        // Alle Mitgliedschaften inklusive Fraktionen, Ausschüsse und sonstigen Gruppierungen
        Set<MitgliedschaftImpl> alleMitgliedschaften = new HashSet<>();

        // Liste aller Parteien
        List<ParteiImpl> alleParteien = new ArrayList<>();
        alleParteien.add(new ParteiImpl("Plos"));
        alleParteien.add(new ParteiImpl("BÜNDNIS 90/DIE GRÜNEN"));

        // Alle Ausschüsse
        Set<AusschussImpl> alleAusschuesse = new HashSet<>();

        // Alle sonstigen Gruppen, d.h. ohne Parteien, Fraktionen und Ausschüssen
        Set<GruppeImpl> sonstGruppen = new HashSet<>();

        // Alle Wahlperioden
        List<WahlperiodeImpl> alleWahlperioden = new ArrayList<>();

        // Alle Wahlkreise
        List<WahlkreisImpl> alleWahlkreise = new ArrayList<>();
        alleWahlkreise.add(new WahlkreisImpl("k. A.", -1));

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

            java.sql.Date geburtsdatum = StringFunctions.toDate(bio.getElementsByTagName("GEBURTSDATUM").item(0).getTextContent());
            String geburtsort = bio.getElementsByTagName("GEBURTSORT").item(0).getTextContent();
            java.sql.Date sterbedatum = StringFunctions.toDate(bio.getElementsByTagName("STERBEDATUM").item(0).getTextContent());

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
            ParteiImpl partei = alleParteien.get(0); // Per Default ist der Abgeordnete parteilos

            // Neue PARTEI des Abgeordneten initialisieren und in alleParteien eingefügen, WENN noch nicht vorhanden

            boolean partei_exists = false;

            for (ParteiImpl p: alleParteien) {
                if (prov_partei.equals("DIE GRÜNEN/BÜNDNIS 90") || p.getLabel().equals(prov_partei)){
                    partei_exists = true;
                    break;
                }
            }

            if (!partei_exists && !prov_partei.isEmpty()) {
                partei = new ParteiImpl(prov_partei);
                alleParteien.add(partei);
            }
            else {
                // Ansonsten passende Partei aus alleParteien heraussuchen und zuweisen, da schon vorhanden
                for (ParteiImpl p_i: alleParteien) {
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

            AbgeordneterImpl abgeordneter = new AbgeordneterImpl(mdbID, nachname, vorname, ortszusatz, adel, anredeTitel,
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
                java.sql.Date startDate = StringFunctions.toDate(wp.getElementsByTagName("MDBWP_VON").item(0).getTextContent());
                java.sql.Date endDate = StringFunctions.toDate(wp.getElementsByTagName("MDBWP_BIS").item(0).getTextContent());

                // WAHLPERIODE initialisieren und in alleWahlperioden eingefügen, WENN noch nicht vorhanden

                WahlperiodeImpl wahlperiode = null;

                boolean wahlperiode_exists = false;

                for (WahlperiodeImpl p: alleWahlperioden) {
                    if (p.getNumber() == nummer) {
                        wahlperiode_exists = true;
                        break;
                    }
                }

                if (!wahlperiode_exists) {
                    wahlperiode = new WahlperiodeImpl("Wahlperiode" + nummer, nummer, startDate, endDate);
                    alleWahlperioden.add(wahlperiode);
                }
                else {
                    // Ansonsten passende Wahlperiode aus alleWahlperioden zuweisen, da schon vorhanden
                    for (WahlperiodeImpl w_i: alleWahlperioden) {
                        if (w_i.getNumber() == nummer) {
                            wahlperiode = w_i;
                            break;
                        }
                    }
                }

                // Neuen WAHLKREIS initialisieren und in alleWahlkreise hinzufügen, WENN nicht vorhanden

                int wahlkreisNummer = StringFunctions.toInt(wp.getElementsByTagName("WKR_NUMMER").item(0).getTextContent());

                WahlkreisImpl wahlkreis = alleWahlkreise.get(0); // Default: ohne Angabe, Wahlkreisnummer = -1

                boolean wahlkreis_exists = false;

                for (WahlkreisImpl wk: alleWahlkreise) {
                    if (wk.getNumber() == wahlkreisNummer) {
                        wahlkreis_exists = true;
                        break;
                    }
                }

                // -1 als Wahlkreisnummer bedeutet hier, dass der Eintrag leer war → kein Wahlkreis erzeugen
                if (!wahlkreis_exists && wahlkreisNummer != -1) {
                    wahlkreis = new WahlkreisImpl("Wahlkreis " + wahlkreisNummer, wahlkreisNummer);
                    alleWahlkreise.add(wahlkreis);
                }
                else {
                    // Ansonsten passenden Wahlkreis aus alleWahlkreise und zuweisen, da schon vorhanden:
                    for (WahlkreisImpl w_i: alleWahlkreise) {
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

                MandatImpl mandat = new MandatImpl(liste + ".MANDAT-" + abgeordneter.getLabel() + "-WP" + nummer,
                        abgeordneter, startDate, endDate, typ, wahlperiode, wahlkreis);

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
                    java.sql.Date mitgliedVon = StringFunctions.toDate(inst.getElementsByTagName("MDBINS_VON").item(0).getTextContent());
                    java.sql.Date mitgliedBis = StringFunctions.toDate(inst.getElementsByTagName("MDBINS_BIS").item(0).getTextContent());

                    // Alle MITGLIEDSCHAFTEN des Abgeordneten erfassen

                    MitgliedschaftImpl mitgliedschaft = null;

                    switch (insArt) {
                        case "Ausschuss":
                        case "Unterausschuss":
                        case "Untersuchungsausschuss":
                            AusschussImpl ausschuss = null;

                            // Neuen AUSSCHUSS initialisieren, in alleAusschuesse sowie in das Mandat einfügen, WENN noch nicht vorhanden

                            boolean ausschuss_exists = false;

                            for (AusschussImpl a: alleAusschuesse) {
                                if (insLang.equals(a.getLabel())){
                                    ausschuss_exists = true;
                                    break;
                                }
                            }

                            if (!ausschuss_exists) {
                                ausschuss = new AusschussImpl(insLang, insArt);
                                alleAusschuesse.add(ausschuss);
                            }
                            else {
                                // Ansonsten passenden Ausschuss aus alleAusschuesse zuweisen, da schon vorhanden:
                                for (AusschussImpl a_i: alleAusschuesse) {
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
                            GruppeImpl gruppe = null;

                            // Neue GRUPPE initialisieren und in sonstGruppen einfügen, WENN noch nicht vorhanden

                            boolean gruppe_exists = false;

                            for (GruppeImpl a: sonstGruppen) {
                                if (insLang.equals(a.getLabel())){
                                    gruppe_exists = true;
                                    break;
                                }
                            }

                            if (!gruppe_exists) {
                                gruppe = new GruppeImpl(insLang);
                                sonstGruppen.add(gruppe);
                            }
                            else {
                                // Ansonsten passende Gruppe aus sonstGruppen zuweisen, da schon vorhanden:
                                for (GruppeImpl a_i: sonstGruppen) {
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
                            FraktionImpl fraktion = alleFraktionen.get(0); // Per Default ist der Abgeordnete fraktionslos

                            // Neue FRAKTION initialisieren und in alleFraktionen einfügen, WENN noch nicht vorhanden

                            boolean fraktion_exists = false;

                            for (FraktionImpl a: alleFraktionen) {
                                if (insLang.equals(a.getLabel()) || insLang.equals("Fraktion Die Grünen/Bündnis 90")){
                                    fraktion_exists = true;
                                    break;
                                }
                            }

                            if (!fraktion_exists) {
                                fraktion = new FraktionImpl(insLang);
                                alleFraktionen.add(fraktion);
                            }
                            else {
                                // Ansonsten passende Fraktion aus alleFraktionen heraussuchen, da schon vorhanden:
                                for (FraktionImpl a_i: alleFraktionen) {
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
        alleWahlperioden.sort(Comparator.comparing(WahlperiodeImpl::getNumber));
        this.wahlperioden.addAll(alleWahlperioden);

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
    public Set<Rede> parseSitzung(File sitzung)
            throws ParserConfigurationException, IOException, SAXException, BundestagException, ParseException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(sitzung);

        NodeList reden = document.getElementsByTagName("rede"); // Enthält alle Reden-Knoten

        // Alle zurückzugebenden Reden der Sitzung
        Set<RedeImpl> alleReden = new HashSet<>();

        // Datum der Sitzung (Datum der Rede) einlesen
        Element datumNode = (Element) document.getElementsByTagName("datum").item(0);
        java.sql.Date datum = StringFunctions.toDate(datumNode.getAttribute("date"));

        for (int i=0; i < reden.getLength(); i++) {
            Element rede = (Element) reden.item(i); // i-te Rede der Sitzung

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
                abgeordneter = null;
            }

            // Redetext einlesen
            StringBuilder redeText = new StringBuilder();

            boolean rednerRedet = true;

            NodeList elements = rede.getChildNodes();
            for (int j = 0; j < elements.getLength(); j++) {
                if (elements.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) elements.item(j); // j-ter "Teil" der Rede

                    // Kommentare zählen nicht zur Rede
                    if (element.getTagName().equals("kommentar")) {
                        continue;
                    }
                    // Nach einem "name"-Tag redet i.d.R. der (Vize)-Präsident o.Ä., nicht der Redner
                    // → folgenden Text nicht mit zur Rede nehmen, bis der Redner selbst wieder dran ist
                    if (element.getTagName().equals("name")) {
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

            // Wir speichern nur die Reden, die von Abgeordneten gehalten wurden
            if (abgeordneter != null) {
                RedeImpl redeObj = new RedeImpl(ID, "ID" + ID, abgeordneter, redeText.toString(), datum);
                alleReden.add(redeObj);
            }
        }

        return new HashSet<>(alleReden);
    }

    /**
     * Gibt die entschuldigten Abgeordneten der übergebenen Sitzung zurück
     *
     * @param sitzung die Datei mit der einzulesenden Sitzung
     * @return ein Tupel der Form (Datum der Sitzung, Menge an fehlenden Abgeordneten)
     */
    public Tuple<java.sql.Date, Set<Abgeordneter>> getEntschuldigteAbgeordnete(File sitzung) throws ParserConfigurationException, IOException, SAXException, BundestagException, ParseException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(sitzung);

        // Datum einlesen
        java.sql.Date datum = StringFunctions.toDate(document.getElementsByTagName("datum").item(0).getAttributes().item(0).getTextContent());

        // Entschuldigte der Sitzung erfassen
        Element anlageEntschuldigte = (Element) document.getElementsByTagName("anlagen-text").item(0);

        // Falls wir doch nicht im richtigen Block sein sollten:
        if (!anlageEntschuldigte.getAttribute("anlagen-typ").equals("Entschuldigte Abgeordnete")) {
            throw new RuntimeException("Anlage enthält nicht die entschuldigten Abgeordneten");
        }

        // Notwendig, da XML-Dateien teilweise anders aufgebaut:
        Element tbody = (Element) anlageEntschuldigte.getElementsByTagName("tbody").item(0);
        if (tbody.getElementsByTagName("tr").getLength() <= 1) {
            tbody = (Element) anlageEntschuldigte.getElementsByTagName("tbody").item(1);
        }

        Set<Abgeordneter> entschuldigteAbgeordnete = new HashSet<>();

        NodeList entschuldigte = tbody.getElementsByTagName("tr");

        for (int i = 0; i < entschuldigte.getLength(); i++) {
            if (entschuldigte.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element entschuldigterAbgeordnete = (Element) entschuldigte.item(i);
                String nachnameVorname = entschuldigterAbgeordnete.getElementsByTagName("td").item(0).getTextContent();
                String nachname = nachnameVorname.split(", ")[0];
                String vorname = nachnameVorname.split(", ")[1];

                String partei = entschuldigterAbgeordnete.getElementsByTagName("td").item(1).getTextContent();

                try {
                    entschuldigteAbgeordnete.add(getAbgeordneterByName(nachname, vorname));
                } catch (AbgeordneterNotFoundException e) {
                    continue;
                }
            }
        }
        return new Tuple<>(datum, entschuldigteAbgeordnete);
    }

    /**
     * Parst eine Abstimmung, dessen Ergebnisse in einer .xlsx gespeichert sind
     *
     * @param pathToXLSX der Pfad zur einzulesenden XLSX-Datei mit den Abstimmungsergebnissen einer Abstimmung
     */
    public void parseAbstimmungXLSX(String pathToXLSX) throws IOException, InvalidFormatException, BundestagException {
        File xlsx = new File(pathToXLSX);

        Sheet sheet;
        try (Workbook workbook = new XSSFWorkbook(xlsx)) {
            sheet = workbook.getSheetAt(0);
        }

        // Jede einzelne Stimme der Abstimmung auswerten und wenn möglich auf den Abgeordneten mappen
        String label = xlsx.getName().substring(0, xlsx.getName().length() - 5);
        int wahlperiode = StringFunctions.toInt(String.valueOf(sheet.getRow(2).getCell(0)));

        int index = 7;

        // Falls AbgNr-Spalte vorhanden:
        if (sheet.getRow(0).getLastCellNum() == 15) {
            index = 8;
        }

        for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
            String nachname = String.valueOf(sheet.getRow(i).getCell(4));
            String vorname = String.valueOf(sheet.getRow(i).getCell(5));
            String beschreibung = xlsx.getName().substring(12, xlsx.getName().length() - 5);

            Types.ABSTIMMUNG ergebnis = Types.ABSTIMMUNG.NONE;
            Types.ABSTIMMUNG[] stimmen = new Types.ABSTIMMUNG[] {Types.ABSTIMMUNG.JA, Types.ABSTIMMUNG.NEIN, Types.ABSTIMMUNG.ENTHALTUNG};
            for (int j = index; j < index + 3; j++) {
                if (sheet.getRow(i).getCell(j).getNumericCellValue() == 1) {
                    if (index == 7) {
                        ergebnis = stimmen[j-7];
                    }
                    else {
                        ergebnis = stimmen[j-8];
                    }
                }
            }
            AbgeordneterImpl abgeordneter;
            try {
                abgeordneter = getAbgeordneterByWahlperiodeAndName(wahlperiode, nachname, vorname);
            }
            catch (AbgeordneterNotFoundException e) {abgeordneter = null;}

            if (!(abgeordneter == null)) {
                abgeordneter.addAbstimmung(new AbstimmungImpl(label, abgeordneter, ergebnis, beschreibung));
            }
        }
    }

    /**
     * Parst eine Abstimmung, dessen Ergebnisse in einer .xls gespeichert sind
     *
     * @param pathToXLS der Pfad zur einzulesenden XLS-Datei mit den Abstimmungsergebnissen einer Abstimmung
     */
    public void parseAbstimmungXLS(String pathToXLS) throws IOException, BundestagException, BiffException {
        File xls = new File(pathToXLS);

        jxl.Workbook workbook = jxl.Workbook.getWorkbook(xls);

        jxl.Sheet sheet = workbook.getSheet(0);

        String label = xls.getName().substring(0, xls.getName().length() - 5);
        int wahlperiode = StringFunctions.toInt(sheet.getCell(0, 2).getContents());

        // Jede einzelne Stimme der Abstimmung auswerten und wenn möglich auf den Abgeordneten mappen
        int index = 7;

        // Falls AbgNr-Spalte vorhanden:
        if (sheet.getColumns() == 15) {
            index = 8;
        }

        for (int i = 1; i < sheet.getRows(); i++) {
            String nachname = sheet.getCell(4, i).getContents();
            String vorname = sheet.getCell(5, i).getContents();
            String beschreibung = xls.getName().substring(12, xls.getName().length() - 5);

            Types.ABSTIMMUNG ergebnis = Types.ABSTIMMUNG.NONE;
            Types.ABSTIMMUNG[] stimmen = new Types.ABSTIMMUNG[] {Types.ABSTIMMUNG.JA, Types.ABSTIMMUNG.NEIN, Types.ABSTIMMUNG.ENTHALTUNG};
            for (int j = index; j < index + 3; j++) {
                if (sheet.getCell(j, i).getContents().equals("1")) {
                    if (index == 7) {
                        ergebnis = stimmen[j-7];
                    }
                    else {
                        ergebnis = stimmen[j-8];
                    }
                }
            }
            AbgeordneterImpl abgeordneter;
            try {
                abgeordneter = getAbgeordneterByWahlperiodeAndName(wahlperiode, nachname, vorname);
            }
            catch (AbgeordneterNotFoundException e) {abgeordneter = null;}

            if (!(abgeordneter == null)) {
                abgeordneter.addAbstimmung(new AbstimmungImpl(label, abgeordneter, ergebnis, beschreibung));
            }
        }
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
     * @return Gibt eine Liste aller Mitgliedschaften zurück
     */
    @Override
    public Set<Mitgliedschaft> listMitgliedschaften() {
        return this.mitgliedschaften;
    }

    /**
     * @return Gibt eine Liste aller eingelesenen Reden zurück
     */
    public List<Rede> listReden() {
        return this.reden;
    }

    /**
     * @return Gibt alle Abwesenheiten aller Sitzungen zurück
     */
    public Set<Tuple<java.sql.Date, Set<Abgeordneter>>> getAbwesenheiten() {
        return this.abwesenheiten;
    }

    /**
     * @return Gibt eine Liste aller erfassten Wahlperioden zurück
     */
    public List<Wahlperiode> getWahlperioden() {
        return this.wahlperioden;
    }

    /**
     * Gibt einen Abgeordneten nach ID zurück
     *
     * @param ID die ID des zu findenden Abgeordneten
     * @return der Abgeordnete mit der übergebenen ID
     * @throws AbgeordneterNotFoundException falls der Abgeordnete mit der angegebenen ID nicht existiert
     */
    public AbgeordneterImpl getAbgeordneterByID(int ID) throws AbgeordneterNotFoundException {
        for (Iterator<Abgeordneter> iterator = this.abgeordnete.iterator(); iterator.hasNext(); ) {
            Abgeordneter abgeordneter = iterator.next();
            if (abgeordneter.getID().equals(ID)) {
                return (AbgeordneterImpl) abgeordneter;
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
    public AbgeordneterImpl getAbgeordneterByWahlperiodeAndName(int wahlperiode, String nachname, String vorname) throws AbgeordneterNotFoundException {
        WahlperiodeImpl fakeWahlperiode = new WahlperiodeImpl("", wahlperiode, null, null); // nur zum Aufruf von hasMandat()
        for (Iterator<Abgeordneter> iterator = this.abgeordnete.iterator(); iterator.hasNext(); ) {
            Abgeordneter abgeordneter = iterator.next();
            if (abgeordneter.hasMandat(fakeWahlperiode)
                    && StringFunctions.simplify(abgeordneter.getName()).equals(StringFunctions.simplify(nachname))
                    && StringFunctions.simplify(abgeordneter.getVorname()).equals(StringFunctions.simplify(vorname))) {
                return (AbgeordneterImpl) abgeordneter;
            }
        }
        throw new AbgeordneterNotFoundException();
    }

    /**
     * Gibt einen Abgeordneten nach Vor-und Nachname zurück
     *
     * @param nachname der Nachname des zu zurückzugebenden Abgeordneten
     * @param vorname der Vorname des zu zurückzugebenden Abgeordneten
     * @return der (hoffentlich) passende Abgeordnete
     */
    public AbgeordneterImpl getAbgeordneterByName(String nachname, String vorname) throws BundestagException {

        for (Iterator<Abgeordneter> iterator = this.abgeordnete.iterator(); iterator.hasNext(); ) {
            Abgeordneter abgeordneter = iterator.next();
            if (StringFunctions.simplify(abgeordneter.getName()).equals(StringFunctions.simplify(nachname))
                    && StringFunctions.simplify(abgeordneter.getVorname()).equals(StringFunctions.simplify(vorname))) {
                return (AbgeordneterImpl) abgeordneter;
            }
        }
        throw new AbgeordneterNotFoundException();
    }

    /**
     * Gibt die Rede mit der gegebenen ID zurück
     *
     * @param ID die ID der zurückzugebenden Rede
     * @return die Rede mit der übergebenen ID
     * @throws RedeNotFoundException falls die Rede mit der ID nicht existiert
     */
    public Rede getRedeByID(int ID) throws RedeNotFoundException {
        for (Rede rede : this.reden) {
            if (rede.getID().equals(ID)) {
                return rede;
            }
        }
        throw new RedeNotFoundException ("Es existiert keine Rede mit der ID " + ID);
    }
}
