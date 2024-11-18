package org.texttechnologylab.project.Stud1.Uebung2.data;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.texttechnologylab.project.Stud1.Uebung2.exceptions.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud1.Uebung2.exceptions.BadDataFormatException;
import org.texttechnologylab.project.Stud1.Uebung2.exceptions.WahlperiodeNotFoundException;
import org.texttechnologylab.project.data.*;
import org.texttechnologylab.project.exception.BundestagException;
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
import java.util.*;

/**
 * Private Implementierung der BundestagFactory
 */
class BundestagFactoryImpl implements BundestagFactory {
    private final Map<Integer, AbgeordneterImpl> abgeordnete;
    private final Map<String, ParteiImpl> parteien;
    private final Map<Integer, WahlperiodeImpl> wahlperioden;
    private final Map<Integer, WahlkreisImpl> wahlkreise;
    private final Map<String, LandeslisteImpl> landeslisten;
    private final Map<String, FraktionImpl> fraktionen;
    private final Map<String, AusschussImpl> ausschuesse;
    private final Map<String, GruppeImpl> sonstigeGruppen;
    private final Set<Mitgliedschaft> mitgliedschaften;
    private final Set<ZeitlicheAbstimmung> abstimmungen;
    private final Set<Rede> reden;
    private final Set<Fehltag> fehltage;

    /**
     * Default-Konstruktor für BundestagFactoryImpl
     */
    BundestagFactoryImpl() {
        abgeordnete = new HashMap<>();
        parteien = new HashMap<>();
        wahlperioden = new HashMap<>();
        wahlkreise = new HashMap<>();
        landeslisten = new HashMap<>();
        fraktionen = new HashMap<>();
        ausschuesse = new HashMap<>();
        sonstigeGruppen = new HashMap<>();
        mitgliedschaften = new HashSet<>();
        abstimmungen = new HashSet<>();
        reden = new HashSet<>();
        fehltage = new HashSet<>();
    }

    /**
     * Hilfsfunktion, um einen String aus einem Textelement zu erhalten
     *
     * @param element Element
     * @param name    Name des Subelements
     * @return Text des Subelements
     */
    // In Kotlin we could use extension functions here, but alas this is Java.
    private static String elementGetElementTextByTagName(Element element, String name) {
        return element.getElementsByTagName(name).item(0).getTextContent();
    }

    /**
     * Hilfsfunktion, um ein Datum aus einem String zu erhalten
     *
     * @param date Datum als dd.mm.yyyy
     * @return Datum als java.sql.Date
     */
    private static Date makeDateFromString(String date) {
        if (date.isEmpty()) return null;

        String[] dates = date.split("\\.");
        return Date.valueOf(dates[2] + "-" + dates[1] + "-" + dates[0]);
    }

    /**
     * Hilfsfunktion, um ein Datum aus einem Textelement zu erhalten
     *
     * @param element Element
     * @param name    Name des Subelements
     * @return Datum als java.sql.Date
     */
    private static Date elementGetElementDateByTagName(Element element, String name) {
        return makeDateFromString(elementGetElementTextByTagName(element, name));
    }

    /**
     * Hilfsfunktion, um eine Zahl aus einem Textelement zu erhalten
     *
     * @param element Element
     * @param name    Name des Subelements
     * @return gesuchter Int
     */
    private static Integer elementGetElementIntByTagName(Element element, String name) {
        String text = elementGetElementTextByTagName(element, name);
        return Integer.parseInt(text);
    }

    /**
     * Private Hilfsfunktion, um das Root-Element einer XML-Datei im DOM zu bekommen
     *
     * @param xmlFile Die XML-Datei
     * @return Das Root-Element
     * @throws BadDataFormatException Wenn die Datei nicht eingelesen werden konnte
     */
    private static Element xmlGetRootElement(File xmlFile) throws BadDataFormatException {
        Element root;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            root = document.getDocumentElement();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException | SAXException e) {
            throw new BadDataFormatException("Error parsing '" + xmlFile.getAbsolutePath() + "'", e);
        }
        return root;
    }

    /**
     * @param stammdatenFile MDB_STAMMDATEN.XML-Datei
     * @throws BadDataFormatException Wenn die MDB_STAMMDATEN.DTD-Datei fehlt, die stammdatenFile-Datei falsch formatiert ist, benötigte Elemente nicht enthält, unbekannte Elemente enthält, etc.
     */
    @Override
    public void readAbgeordnete(File stammdatenFile) throws BadDataFormatException {
        Element root = xmlGetRootElement(stammdatenFile);

        NodeList mdbs = root.getChildNodes();
        for (int i = 0; i < mdbs.getLength(); i++) {

            Node mdbNode = mdbs.item(i);
            if (mdbNode.getNodeType() != Node.ELEMENT_NODE || mdbNode.getNodeName().equals("VERSION")) {
                continue;
            }
            if (!mdbNode.getNodeName().equals("MDB")) {
                throw new BadDataFormatException("Unexpected token '" + mdbNode.getNodeName() + "' in MDB list");
            }
            Element mdb = (Element) mdbNode;

            Integer id = elementGetElementIntByTagName(mdb, "ID");

            Element namenElement = (Element) mdb.getElementsByTagName("NAMEN").item(0);
            List<AbgeordneterName> namen = new ArrayList<>();
            NodeList namenNodes = namenElement.getElementsByTagName("NAME");
            for (int j = 0; j < namenNodes.getLength(); j++) {
                Element name = (Element) namenNodes.item(j);
                String nachname = elementGetElementTextByTagName(name, "NACHNAME");
                String vorname = elementGetElementTextByTagName(name, "VORNAME");
                String ortszusatz = elementGetElementTextByTagName(name, "ORTSZUSATZ");
                String adelssuffix = elementGetElementTextByTagName(name, "ADEL");
                String anrede = elementGetElementTextByTagName(name, "ANREDE_TITEL");
                String akadTitel = elementGetElementTextByTagName(name, "AKAD_TITEL");
                Date von = elementGetElementDateByTagName(name, "HISTORIE_VON");
                Date bis = elementGetElementDateByTagName(name, "HISTORIE_BIS");
                namen.add(new AbgeordneterName(vorname, nachname, ortszusatz, adelssuffix, anrede, akadTitel, von, bis));
            }

            Element biografischeAngaben = (Element) mdb.getElementsByTagName("BIOGRAFISCHE_ANGABEN").item(0);
            Date geburtsdatum = elementGetElementDateByTagName(biografischeAngaben, "GEBURTSDATUM");
            String geburtsort = elementGetElementTextByTagName(biografischeAngaben, "GEBURTSORT");
            Date sterbedatum = elementGetElementDateByTagName(biografischeAngaben, "STERBEDATUM");
            Types.GESCHLECHT geschlecht;
            {
                String geschlechtString = elementGetElementTextByTagName(biografischeAngaben, "GESCHLECHT");
                if (geschlechtString.equals("männlich")) {
                    geschlecht = Types.GESCHLECHT.MAENNLICH;
                } else if (geschlechtString.equals("weiblich")) {
                    geschlecht = Types.GESCHLECHT.WEIBLICH;
                } else {
                    throw new BadDataFormatException("Geschlecht '" + geschlechtString + "' nicht bekannt.");
                }
            }
            // Familienstand doesn't appear to be required. //String familienstand = elementGetElementTextByTagName(biografischeAngaben, "FAMILIENSTAND");
            String religion = elementGetElementTextByTagName(biografischeAngaben, "RELIGION");
            String beruf = elementGetElementTextByTagName(biografischeAngaben, "BERUF");
            String vita = elementGetElementTextByTagName(biografischeAngaben, "VITA_KURZ");
            ParteiImpl partei;
            {
                String parteiKurz = elementGetElementTextByTagName(biografischeAngaben, "PARTEI_KURZ");
                // Für einige ist parteilos nicht "Plos", sondern der leere String
                if (parteiKurz.isEmpty()) {
                    parteiKurz = "Plos";
                }
                if (parteien.containsKey(parteiKurz)) {
                    partei = parteien.get(parteiKurz);
                } else {
                    partei = new ParteiImpl(parteiKurz);
                    parteien.put(parteiKurz, partei);
                }
            }

            AbgeordneterImpl abgeordneter;
            try {
                abgeordneter = new AbgeordneterImpl(id, namen, geburtsdatum, geburtsort, sterbedatum, geschlecht, religion, beruf, vita, partei);
            } catch (BundestagException e) {
                throw new BadDataFormatException(e);
            }
            NodeList wahlperiodenNode = ((Element) mdb.getElementsByTagName("WAHLPERIODEN").item(0)).getElementsByTagName("WAHLPERIODE");
            for (int j = 0; j < wahlperiodenNode.getLength(); j++) {
                Element wahlperiodeElement = (Element) wahlperiodenNode.item(j);
                Integer wpNumber = elementGetElementIntByTagName(wahlperiodeElement, "WP");
                WahlperiodeImpl wahlperiode;
                Date mandatStartDate = elementGetElementDateByTagName(wahlperiodeElement, "MDBWP_VON");
                Date mandatEndDate = elementGetElementDateByTagName(wahlperiodeElement, "MDBWP_BIS");
                if (wahlperioden.containsKey(wpNumber)) {
                    wahlperiode = wahlperioden.get(wpNumber);

                    /* Wir wissen die Wahlperiode nur durch die Mandatszeit. Wenn
                     * ein Mandat in der Wahlperiode früher beginnt als die Wahlperiode,
                     * muss die Wahlperiode auch früher beginnen. Daher müssen wir
                     * hier nach und nach die Daten tauschen. */
                    Date wahlperiodeStartDate = wahlperiode.getStartDate();
                    Date wahlperiodeEndDate = wahlperiode.getEndDate();
                    if (wahlperiodeStartDate.after(mandatStartDate)) {
                        wahlperiode.setStartDate(mandatStartDate);
                    }
                    if ((wahlperiodeEndDate != null && mandatEndDate == null) || (wahlperiodeEndDate != null && wahlperiodeEndDate.before(mandatEndDate))) {
                        wahlperiode.setEndDate(mandatEndDate);
                    }
                } else {
                    wahlperiode = new WahlperiodeImpl(wpNumber, mandatStartDate, mandatEndDate);
                    wahlperioden.put(wpNumber, wahlperiode);
                }

                String mandatsartString = elementGetElementTextByTagName(wahlperiodeElement, "MANDATSART");
                Types.MANDAT mandatsart;
                if (mandatsartString.equals("Landesliste")) {
                    mandatsart = Types.MANDAT.LANDESLISTE;
                } else if (mandatsartString.equals("Direktwahl")) {
                    mandatsart = Types.MANDAT.DIREKTWAHL;
                } else if (mandatsartString.equals("Volkskammer") || mandatsartString.isEmpty()) {
                    mandatsart = null;
                } else {
                    throw new BadDataFormatException("Mandartsart '" + mandatsartString + "' not known");
                }

                WahlkreisImpl wahlkreis;
                if (mandatsart == Types.MANDAT.DIREKTWAHL) {
                    Integer wahlkreisNummer = elementGetElementIntByTagName(wahlperiodeElement, "WKR_NUMMER");
                    if (wahlkreise.containsKey(wahlkreisNummer)) {
                        wahlkreis = wahlkreise.get(wahlkreisNummer);
                    } else {
                        wahlkreis = new WahlkreisImpl("Wahlkreis " + wahlkreisNummer, wahlkreisNummer);
                        wahlkreise.put(wahlkreisNummer, wahlkreis);
                    }
                } else {
                    wahlkreis = null;
                }

                LandeslisteImpl landesliste;
                if (mandatsart == Types.MANDAT.LANDESLISTE) {
                    String liste = elementGetElementTextByTagName(wahlperiodeElement, "LISTE");
                    if (landeslisten.containsKey(liste)) {
                        landesliste = landeslisten.get(liste);
                    } else {
                        landesliste = new LandeslisteImpl(liste);
                        landeslisten.put(liste, landesliste);
                    }
                } else {
                    landesliste = null;
                }

                Set<Fraktion> mandatsFraktionen = new HashSet<>();
                Set<Ausschuss> mandatsAusschuesse = new HashSet<>();
                Set<Mitgliedschaft> mandatsMitgliedschaften = new HashSet<>();

                /* Institutionen tracken */
                NodeList institutionenList = ((Element) wahlperiodeElement.getElementsByTagName("INSTITUTIONEN").item(0)).getElementsByTagName("INSTITUTION");
                for (int k = 0; k < institutionenList.getLength(); k++) {
                    Element institution = (Element) institutionenList.item(k);
                    String institutionsArt = elementGetElementTextByTagName(institution, "INSART_LANG");
                    String institutionsName = elementGetElementTextByTagName(institution, "INS_LANG");

                    Date mitgliedschaftVon = elementGetElementDateByTagName(institution, "MDBINS_VON");
                    if (mitgliedschaftVon == null) {
                        mitgliedschaftVon = mandatStartDate;
                    }
                    Date mitgliedschaftBis = elementGetElementDateByTagName(institution, "MDBINS_BIS");
                    if (mitgliedschaftBis == null) {
                        mitgliedschaftBis = mandatEndDate;
                    }

                    GruppeImpl gruppe;
                    String funktion = elementGetElementTextByTagName(institution, "FKT_LANG");
                    if (funktion.isEmpty()) {
                        funktion = null;
                    }
                    switch (institutionsArt) {
                        case "Fraktion/Gruppe":
                            FraktionImpl fraktion;
                            if (fraktionen.containsKey(institutionsName)) {
                                fraktion = fraktionen.get(institutionsName);
                            } else {
                                fraktion = new FraktionImpl(institutionsName);
                                fraktionen.put(institutionsName, fraktion);
                            }
                            fraktion.addMember(abgeordneter, funktion);
                            mandatsFraktionen.add(fraktion);
                            gruppe = fraktion;
                            break;
                        case "Ausschuss":
                        case "Unterausschuss":
                        case "Untersuchungsausschuss":
                            AusschussImpl ausschuss;
                            if (ausschuesse.containsKey(institutionsName)) {
                                ausschuss = ausschuesse.get(institutionsName);
                            } else {
                                ausschuss = new AusschussImpl(institutionsName, institutionsArt);
                                ausschuesse.put(institutionsName, ausschuss);
                            }
                            ausschuss.addMember(abgeordneter);
                            mandatsAusschuesse.add(ausschuss);
                            gruppe = ausschuss;
                            break;
                        case "Parlamentariergruppen":
                        case "Deutscher Bundestag":
                        case "Sonstiges Gremium":
                        case "Ministerium":
                        case "Enquete-Kommission":
                            if (sonstigeGruppen.containsKey(institutionsName)) {
                                gruppe = sonstigeGruppen.get(institutionsName);
                            } else {
                                gruppe = new GruppeImpl(institutionsName);
                                sonstigeGruppen.put(institutionsName, gruppe);
                            }
                            gruppe.addMember(abgeordneter);
                            break;
                        default:
                            throw new BadDataFormatException("Unbekannte Institutionsart '" + institutionsArt + "'.");
                    }

                    /* Mitgliedschaft tracken */
                    Mitgliedschaft mitgliedschaft = new MitgliedschaftImpl(abgeordneter, funktion, mitgliedschaftVon, mitgliedschaftBis, gruppe, wahlperiode);
                    mitgliedschaften.add(mitgliedschaft);
                    mandatsMitgliedschaften.add(mitgliedschaft);
                    abgeordneter.addMitgliedschaft(mitgliedschaft);
                }

                Mandat mandat = new MandatImpl(abgeordneter, mandatStartDate, mandatEndDate, mandatsFraktionen, mandatsAusschuesse, mandatsMitgliedschaften, mandatsart, wahlperiode, wahlkreis);
                if (wahlkreis != null) {
                    wahlkreis.addMandat(mandat);
                }
                if (landesliste != null) {
                    landesliste.addMandat(mandat);
                }
                wahlperiode.addMandat(mandat);
                abgeordneter.addMandat(mandat);
            }


            // Circular dependencies :party:
            partei.addMember(abgeordneter);
            abgeordnete.put(id, abgeordneter);
        }
    }

    /**
     * Liest Reden einer Redendatei ein.
     *
     * @param protokollDatei XML-Datei mit den Reden
     * @throws AbgeordneterNotFoundException Wenn ein Redner nicht in der Menge der Abgeordneten zu finden ist
     * @throws BadDataFormatException        Wenn die dbtplenarprotokoll.dtd-Datei fehlt, die XML-Datei falsch formatiert ist, benötigte Elemente nicht enthält, unbekannte Elemente enthält, etc.
     */
    @Override
    public void readProtokoll(File protokollDatei) throws AbgeordneterNotFoundException, BadDataFormatException {
        Element protokoll = xmlGetRootElement(protokollDatei);

        Date sitzungsdatum = makeDateFromString(protokoll.getAttribute("sitzung-datum"));
        Element sitzungsverlauf = (Element) protokoll.getElementsByTagName("sitzungsverlauf").item(0);

        NodeList sitzungsteile = sitzungsverlauf.getChildNodes();
        for (int i = 0; i < sitzungsteile.getLength(); i++) {
            if (sitzungsteile.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element sitzungsteil = (Element) sitzungsteile.item(i);
            if (!sitzungsteil.getNodeName().equals("sitzungsbeginn") && !sitzungsteil.getNodeName().equals("tagesordnungspunkt")) {
                continue;
            }

            NodeList redenList = sitzungsteil.getElementsByTagName("rede");
            for (int j = 0; j < redenList.getLength(); j++) {
                Element redeElement = (Element) redenList.item(j);
                String redeId = redeElement.getAttribute("id");

                Element rednerElement = (Element) redeElement.getElementsByTagName("redner").item(0);
                Integer rednerId = Integer.parseInt(rednerElement.getAttribute("id"));

                boolean isRedner = false;
                List<String> redeAusschnitte = new ArrayList<>();
                NodeList redeChildNodes = redeElement.getChildNodes();
                for (int k = 0; k < redeChildNodes.getLength(); k++) {

                    if (redeChildNodes.item(k).getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    Element childNode = (Element) redeChildNodes.item(k);

                    if (childNode.getNodeName().equals("kommentar")) {
                        continue;
                    } else if (childNode.getNodeName().equals("name")) {
                        isRedner = false;
                        continue;
                    } else if (!childNode.getNodeName().equals("p")) {
                        throw new BadDataFormatException("Unbekannter Node-Typ '" + childNode.getNodeName() + "' in Rede");
                    }

                    String klasse = childNode.getAttribute("klasse");
                    switch (klasse) {
                        case "redner":
                            isRedner = true;
                            break;
                        case "O":
                        case "J":
                        case "J_1":
                        case "Z": // Zitat?
                        case "": // Passiert auch manchmal?
                            if (isRedner) {
                                redeAusschnitte.add(childNode.getTextContent());
                            }
                            break;
                        case "T_Beratung":
                            break;
                        default:
                            throw new BadDataFormatException("Unbekannte Klasse '" + klasse + "' in Rede");
                    }
                }

                if (!abgeordnete.containsKey(rednerId) && !rednerId.toString().startsWith("9999") /* Gäste, die wir ignorieren */) {
                    throw new AbgeordneterNotFoundException("Abgeordnter mit ID '" + rednerId + "' wurde nicht gefunden");
                }
                Abgeordneter redner = abgeordnete.get(rednerId);

                Rede rede = new RedeImpl(redeId, redner, redeAusschnitte, sitzungsdatum);
                reden.add(rede);
            }
        }

        NodeList anlagen = ((Element) protokoll.getElementsByTagName("anlagen").item(0)).getElementsByTagName("anlage");
        for (int i = 0; i < anlagen.getLength(); i++) {
            Node item = anlagen.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element anlage = (Element) item;
            Element anlangenText = (Element) anlage.getElementsByTagName("anlagen-text").item(0);

            if (!anlangenText.getAttribute("anlagen-typ").equals("Entschuldigte Abgeordnete")) {
                continue;
            }

            // This isn't html, why is this even a formatted table in the first place
            // AND WHY FOR FUCKS SAKE DOES ONE TABLE HAVE MULTIPLE TBODY TAGS
            NodeList tbodys = anlangenText.getElementsByTagName("tbody");
            NodeList rows = ((Element) tbodys.item(tbodys.getLength() - 1)).getElementsByTagName("tr");
            for (int j = 0; j < rows.getLength(); j++) {
                String name = ((Element) rows.item(j)).getElementsByTagName("td").item(0).getTextContent();
                name = name.replace("*", "");
                String[] nameParts = name.split(", ");
                AbgeordneterImpl abgeordneter = getAbgeordneterByName(nameParts[1], nameParts[0], false);
                if (abgeordneter == null) {
                    throw new AbgeordneterNotFoundException("Entschuldigte*r Abgeordnete*r '" + name + "' konnte nicht gefunden werden.");
                }
                fehltage.add(new FehltagImpl(sitzungsdatum, abgeordneter));
            }
        }
    }

    /**
     * Private Hilfsfunktion, um (hoffentlich ohne potenzielle race-conditions) Abstimmungen zur Liste hinzuzufügen
     *
     * @param wahlperiode             Wahlperiode der Abstimmung
     * @param abgeordneter            Abgeordneter der Abstimmung
     * @param ergebnis                Abstimmungsergebnis
     * @param abstimmungsBeschreibung Beschreibung der Abstimmung
     */
    synchronized private void makeAndAddAbstimmung(Wahlperiode wahlperiode, AbgeordneterImpl abgeordneter, Types.ABSTIMMUNG ergebnis, String abstimmungsBeschreibung) {
        AbstimmungImpl abstimmung = new AbstimmungImpl(wahlperiode, abgeordneter, ergebnis, abstimmungsBeschreibung);
        abgeordneter.addAbstimmung(abstimmung);
        abstimmungen.add(abstimmung);
    }

    /**
     * Private Hilfsfunktion, um den Tabellenkopf von XLS-Dateien zu verifizieren
     *
     * @param sheet    Tabelle
     * @param i        Spaltenzahl (Zeile 0 wird angenommen)
     * @param erwartet erwarteter Spaltenname
     * @throws BadDataFormatException Wenn der Spaltenname nicht übereinstimmt
     */
    private void verifyColumnXls(Sheet sheet, int i, String erwartet) throws BadDataFormatException {
        if (!sheet.getCell(i, 0).getContents().equals(erwartet)) {
            throw new BadDataFormatException("Unerwartete Spalte '" + sheet.getCell(i, 0).getContents() + "' an Stelle '" + i + "' in Tabelle (erwartet: '" + erwartet + "')");
        }
    }

    /**
     * Lokale Hilfsfunktion, die einen Abgeordneten nach seinem Namen sucht.
     *
     * @param vorname  Vorname des Abgeordneten
     * @param nachname Nachname des Abgeordneten
     * @param nurAbc   Ob Umlaute wie z.B. ä/ö/ü verboten sein sollen
     * @return Den Abgeordneten. null, wenn keiner gefunden werden kann
     */
    private AbgeordneterImpl getAbgeordneterByName(String vorname, String nachname, boolean nurAbc) {
        String nachnameFormatted = nachname.toLowerCase().replace("ğ", "g").replace("ć", "c").replace("ă", "a");
        String vornameFormatted = vorname.toLowerCase().replace("ğ", "g").replace("ć", "c").replace("ă", "a");
        if (nurAbc) {
            nachnameFormatted = nachnameFormatted.replaceAll("[^a-zA-Z ()]", "");
            vornameFormatted = vornameFormatted.replaceAll("[^a-zA-Z ()]", "");
        }

        AbgeordneterImpl abgeordneter = null;
        for (AbgeordneterImpl potentiellerAbgeordneter : abgeordnete.values()) {
            for (AbgeordneterName name : potentiellerAbgeordneter.getNamen()) {

                String potentiellerNachname = name.getNachname();
                String potentiellerVorname = name.getVorname();
                potentiellerVorname = potentiellerVorname.toLowerCase().replace("ğ", "g").replace("ć", "c").replace("ă", "a");
                potentiellerNachname = potentiellerNachname.toLowerCase().replace("ğ", "g").replace("ć", "c").replace("ă", "a");
                if (nurAbc) {
                    potentiellerVorname = potentiellerVorname.replaceAll("[^a-zA-Z ()]", "");
                    potentiellerNachname = potentiellerNachname.replaceAll("[^a-zA-Z ()]", "");
                }

                // Die scheiß Nazis schreiben ihre zweiten Vornamen in Abstimmungen, aber nicht in Stammdaten (oder andersherum). Daher müssen wir auf "startsWith" testen.
                if ((vornameFormatted.contains(potentiellerVorname) && nachnameFormatted.contains(potentiellerNachname)) || (potentiellerVorname.contains(vornameFormatted) && potentiellerNachname.contains(nachnameFormatted))) {
                    abgeordneter = potentiellerAbgeordneter;
                    break;
                }
            }
            if (abgeordneter != null) {
                break;
            }
        }
        return abgeordneter;
    }

    /**
     * Liest eine .xls-Abstimmungs-Datei ein
     *
     * @param abstimmungFile .xls-Datei mit einer Abstimmung
     * @throws AbgeordneterNotFoundException Wenn ein abstimmender Abgeordneter nicht bekannt ist
     */
    @Override
    public void readAbstimmungXls(File abstimmungFile) throws AbgeordneterNotFoundException, BadDataFormatException, WahlperiodeNotFoundException {
        Workbook workbook;
        try {
            WorkbookSettings settings = new WorkbookSettings();
            settings.setSuppressWarnings(true);
            workbook = Workbook.getWorkbook(abstimmungFile, settings);
        } catch (IOException | BiffException | StringIndexOutOfBoundsException e) {
            throw new BadDataFormatException(e);
        }

        Sheet sheet = workbook.getSheet(0);
        int rowNum = sheet.getRows();
        if (sheet.getColumns() != 14 && sheet.getColumns() != 13) {
            throw new BadDataFormatException("Unerwartete Spaltenzahl '" + sheet.getColumns() + "' in Abstimmungen");
        }

        for (int i = 0; i < rowNum; i++) {
            if (i == 0) {
                verifyColumnXls(sheet, 0, "Wahlperiode");
                verifyColumnXls(sheet, 1, "Sitzungnr");
                verifyColumnXls(sheet, 2, "Abstimmnr");
                verifyColumnXls(sheet, 3, "Fraktion/Gruppe");
                verifyColumnXls(sheet, 4, "Name");
                verifyColumnXls(sheet, 5, "Vorname");
                verifyColumnXls(sheet, 6, "Titel");
                verifyColumnXls(sheet, 7, "ja");
                verifyColumnXls(sheet, 8, "nein");
                verifyColumnXls(sheet, 9, "Enthaltung");
                verifyColumnXls(sheet, 10, "ung�ltig");
                verifyColumnXls(sheet, 11, "nichtabgegeben");
                verifyColumnXls(sheet, 12, "Bezeichnung");
                if (sheet.getColumns() >= 14) {
                    verifyColumnXls(sheet, 13, "Bemerkung");
                }
            } else {
                String nachname = sheet.getCell(4, i).getContents();
                String vorname = sheet.getCell(5, i).getContents();
                AbgeordneterImpl abgeordneter = getAbgeordneterByName(vorname, nachname, true);
                if (abgeordneter == null) {
                    throw new AbgeordneterNotFoundException("Abgeordneter '" + vorname + " " + nachname + "' konnte nicht gefunden werden");
                }

                Types.ABSTIMMUNG ergebnis = Types.ABSTIMMUNG.NONE;
                if (sheet.getCell(7, i).getContents().equals("1")) {
                    ergebnis = Types.ABSTIMMUNG.JA;
                } else if (sheet.getCell(8, i).getContents().equals("1")) {
                    ergebnis = Types.ABSTIMMUNG.NEIN;
                } else if (sheet.getCell(9, i).getContents().equals("1")) {
                    ergebnis = Types.ABSTIMMUNG.ENTHALTUNG;
                }

                int wahlperiodeNr;
                try {
                    wahlperiodeNr = Integer.parseInt(sheet.getCell(0, i).getContents());
                } catch (NumberFormatException e) {
                    throw new WahlperiodeNotFoundException(e);
                }

                if (!wahlperioden.containsKey(wahlperiodeNr)) {
                    throw new WahlperiodeNotFoundException("Wahlperiode '" + wahlperiodeNr + "' ist nicht bekannt");
                }
                Wahlperiode wahlperiode = wahlperioden.get(wahlperiodeNr);

                makeAndAddAbstimmung(wahlperiode, abgeordneter, ergebnis, abstimmungFile.getName().replace(".xls", ""));
            }
        }
    }

    /**
     * Private Hilfsfunktion, um den Tabellenkopf von XLSX-Dateien zu verifizieren
     *
     * @param row      Reihe (i.d.R. die erste Reihe)
     * @param i        Spaltenzahl
     * @param erwartet erwarteter Spaltenname
     * @throws BadDataFormatException Wenn der Spaltenname nicht übereinstimmt
     */
    private void verifyColumnXlsx(Row row, int i, String erwartet) throws BadDataFormatException {
        if (!row.getCell(i).getStringCellValue().equals(erwartet)) {
            throw new BadDataFormatException("Unerwartete Spalte '" + row.getCell(i).getStringCellValue() + "' an Stelle '" + i + "' in Tabelle (erwartet: '" + erwartet + "')");
        }
    }

    /**
     * Liest eine .xlsx-Abstimmungs-Datei ein
     *
     * @param abstimmungFile .xlsx-Datei mit einer Abstimmung
     * @throws AbgeordneterNotFoundException Wenn ein abstimmender Abgeordneter nicht bekannt ist
     */
    @Override
    public void readAbstimmungXlsx(File abstimmungFile) throws AbgeordneterNotFoundException, BadDataFormatException, WahlperiodeNotFoundException {
        org.apache.poi.ss.usermodel.Workbook workbook;
        try {
            workbook = WorkbookFactory.create(abstimmungFile);
        } catch (IOException e) {
            throw new BadDataFormatException(e);
        }

        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
        int offset = 0;
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);

            /* Aus irgendeinem Grund haben einige Dateien Extra-Reihen am Ende. Die können wir ignorieren. */
            if (row == null) {
                continue;
            }
            if (row.getLastCellNum() < 12 + offset) {
                throw new BadDataFormatException("Row has too few cells");
            }
            if (i == 0) {
                verifyColumnXlsx(row, 0, "Wahlperiode");
                verifyColumnXlsx(row, 1, "Sitzungnr");
                verifyColumnXlsx(row, 2, "Abstimmnr");
                verifyColumnXlsx(row, 3, "Fraktion/Gruppe");
                if (row.getCell(4).getStringCellValue().equals("AbgNr")) {
                    offset = 1;
                }
                verifyColumnXlsx(row, 4 + offset, "Name");
                verifyColumnXlsx(row, 5 + offset, "Vorname");
                verifyColumnXlsx(row, 6 + offset, "Titel");
                verifyColumnXlsx(row, 7 + offset, "ja");
                verifyColumnXlsx(row, 8 + offset, "nein");
                verifyColumnXlsx(row, 9 + offset, "Enthaltung");
                verifyColumnXlsx(row, 10 + offset, "ungültig");
                verifyColumnXlsx(row, 11 + offset, "nichtabgegeben");
                verifyColumnXlsx(row, 12 + offset, "Bezeichnung");
                verifyColumnXlsx(row, 13 + offset, "Bemerkung");
            } else {
                String nachname = row.getCell(4 + offset).getStringCellValue();
                String vorname = row.getCell(5 + offset).getStringCellValue();
                AbgeordneterImpl abgeordneter = getAbgeordneterByName(vorname, nachname, false);
                if (abgeordneter == null) {
                    throw new AbgeordneterNotFoundException("Abgeordneter '" + vorname + " " + nachname + "' konnte nicht gefunden werden");
                }

                Types.ABSTIMMUNG ergebnis = Types.ABSTIMMUNG.NONE;
                if (row.getCell(7 + offset).getNumericCellValue() == 1) {
                    ergebnis = Types.ABSTIMMUNG.JA;
                } else if (row.getCell(8 + offset).getNumericCellValue() == 1) {
                    ergebnis = Types.ABSTIMMUNG.NEIN;
                } else if (row.getCell(9 + offset).getNumericCellValue() == 1) {
                    ergebnis = Types.ABSTIMMUNG.ENTHALTUNG;
                }

                int wahlperiodeNr = (int) row.getCell(0).getNumericCellValue();

                if (!wahlperioden.containsKey(wahlperiodeNr)) {
                    throw new WahlperiodeNotFoundException("Wahlperiode '" + wahlperiodeNr + "' ist nicht bekannt");
                }
                Wahlperiode wahlperiode = wahlperioden.get(wahlperiodeNr);

                makeAndAddAbstimmung(wahlperiode, abgeordneter, ergebnis, abstimmungFile.getName().replace(".xlsx", ""));
            }
        }
    }

    /**
     * @return Menge aller Abgeordneter
     */
    @Override
    public Set<Abgeordneter> listAbgeordnete() {
        return new HashSet<>(abgeordnete.values());
    }

    /**
     * @return Menge aller Fraktionen
     */
    @Override
    public Set<Fraktion> listFraktionen() {
        return new HashSet<>(fraktionen.values());
    }

    /**
     * @return Menge aller Mitgliedschaften
     */
    @Override
    public Set<Mitgliedschaft> listMitgliedschaften() {
        return new HashSet<>(mitgliedschaften);
    }

    /**
     * Listet alle eingelesenen Reden auf
     *
     * @return Liste aller Reden
     */
    @Override
    public Set<Rede> listReden() {
        return new HashSet<>(reden);
    }

    /**
     * @return Menge aller Parteien
     */
    @Override
    public Set<Partei> listParteien() {
        return new HashSet<>(parteien.values());
    }

    /**
     * Listet alle eingelesenen Ausschuesse auf
     *
     * @return Liste alle Ausschuesse
     */
    @Override
    public Set<Ausschuss> listAusschuesse() {
        return new HashSet<>(ausschuesse.values());
    }

    /**
     * Listet alle eingelesenen Wahlperioden auf
     *
     * @return Liste alle Wahlperioden
     */
    @Override
    public Set<Wahlperiode> listWahlperioden() {
        return new HashSet<>(wahlperioden.values());
    }

    /**
     * Listet alle eingelesenen Wahlkreise auf
     *
     * @return Liste alle Wahlkreise
     */
    @Override
    public Set<Wahlkreis> listWahlkreise() {
        return new HashSet<>(wahlkreise.values());
    }

    /**
     * Listet alle eingelesenen Landeslisten auf
     *
     * @return Liste alle Landeslisten
     */
    @Override
    public Set<Landesliste> listLandeslisten() {
        return new HashSet<>(landeslisten.values());
    }

    /**
     * Listet alle eingelesenen Fehltage auf
     *
     * @return Liste alle Fehltage
     */
    @Override
    public Set<Fehltag> listFehltage() {
        return new HashSet<>(fehltage);
    }
}
