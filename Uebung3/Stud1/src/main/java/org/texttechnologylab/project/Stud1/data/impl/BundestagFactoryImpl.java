package org.texttechnologylab.project.Stud1.data.impl;

import org.texttechnologylab.project.Stud1.data.*;
import org.texttechnologylab.project.Stud1.exceptions.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud1.exceptions.BadDataFormatException;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Private Implementierung der BundestagFactory
 */
public class BundestagFactoryImpl implements BundestagFactory {
    private final Map<String, Abgeordneter_File_Impl> abgeordnete;
    private final Set<Rede> reden;
    private final Set<Sitzung> sitzungen;

    /**
     * Default-Konstruktor für BundestagFactoryImpl
     */
    public BundestagFactoryImpl() {
        abgeordnete = new HashMap<>();
        reden = new HashSet<>();
        sitzungen = new HashSet<>();
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
     * @return Datum
     */
    private static Date makeDateFromString(String date) {
        if (date.isEmpty()) return null;

        String[] dates = date.split("\\.");
        //TODO Check timezone shenanigans
        return java.sql.Date.valueOf(dates[2] + "-" + dates[1] + "-" + dates[0]);
    }

    /**
     * Hilfsfunktion, um ein Datum aus einem Textelement zu erhalten
     *
     * @param element Element
     * @param name    Name des Subelements
     * @return Datum
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

            String id = elementGetElementTextByTagName(mdb, "ID");

            Element namenElement = (Element) mdb.getElementsByTagName("NAMEN").item(0);
            NodeList namenNodes = namenElement.getElementsByTagName("NAME");
            Element name = (Element) namenNodes.item(namenNodes.getLength() - 1);
            String nachname = elementGetElementTextByTagName(name, "NACHNAME");
            String vorname = elementGetElementTextByTagName(name, "VORNAME");
            String ortszusatz = elementGetElementTextByTagName(name, "ORTSZUSATZ");
            String adelssuffix = elementGetElementTextByTagName(name, "ADEL");
            String anrede = elementGetElementTextByTagName(name, "ANREDE_TITEL");
            String akadTitel = elementGetElementTextByTagName(name, "AKAD_TITEL");
            Element biografischeAngaben = (Element) mdb.getElementsByTagName("BIOGRAFISCHE_ANGABEN").item(0);
            Date geburtsdatum = elementGetElementDateByTagName(biografischeAngaben, "GEBURTSDATUM");
            String geburtsort = elementGetElementTextByTagName(biografischeAngaben, "GEBURTSORT");
            Date sterbedatum = elementGetElementDateByTagName(biografischeAngaben, "STERBEDATUM");
            String geschlecht = elementGetElementTextByTagName(biografischeAngaben, "GESCHLECHT");
            // Familienstand doesn't appear to be required. //String familienstand = elementGetElementTextByTagName(biografischeAngaben, "FAMILIENSTAND");
            String religion = elementGetElementTextByTagName(biografischeAngaben, "RELIGION");
            String beruf = elementGetElementTextByTagName(biografischeAngaben, "BERUF");
            String vita = elementGetElementTextByTagName(biografischeAngaben, "VITA_KURZ");
            String partei = elementGetElementTextByTagName(biografischeAngaben, "PARTEI_KURZ");
            /* Für einige ist parteilos nicht "Plos", sondern der leere String.
             * Wir machen einfach überall "Parteilos" draus, ähnlich zu "Fraktionslos". */
            if (partei.equals("Plos") || partei.isEmpty()) {
                partei = "Parteilos";
            }

            String fraktion = null;
            NodeList wahlperiodenNode = ((Element) mdb.getElementsByTagName("WAHLPERIODEN").item(0)).getElementsByTagName("WAHLPERIODE");
            for (int j = 0; j < wahlperiodenNode.getLength(); j++) {
                Element wahlperiodeElement = (Element) wahlperiodenNode.item(j);

                /* Institutionen tracken */
                NodeList institutionenList = ((Element) wahlperiodeElement.getElementsByTagName("INSTITUTIONEN").item(0)).getElementsByTagName("INSTITUTION");
                for (int k = 0; k < institutionenList.getLength(); k++) {
                    Element institution = (Element) institutionenList.item(k);
                    String institutionsArt = elementGetElementTextByTagName(institution, "INSART_LANG");
                    String institutionsName = elementGetElementTextByTagName(institution, "INS_LANG");

                    switch (institutionsArt) {
                        case "Fraktion/Gruppe":
                            // Assume last to be parsed to be the latest one
                            fraktion = institutionsName;
                            break;
                        case "Ausschuss":
                        case "Unterausschuss":
                        case "Untersuchungsausschuss":
                        case "Parlamentariergruppen":
                        case "Deutscher Bundestag":
                        case "Sonstiges Gremium":
                        case "Ministerium":
                        case "Enquete-Kommission":
                            break;
                        default:
                            throw new BadDataFormatException("Unbekannte Institutionsart '" + institutionsArt + "'.");
                    }
                }
            }
            Abgeordneter_File_Impl abgeordneter = new Abgeordneter_File_Impl(id, nachname, vorname, ortszusatz, adelssuffix, anrede, akadTitel, geburtsdatum, geburtsort, sterbedatum, geschlecht, religion, beruf, vita, partei, fraktion);

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

        Set<String> tagesordnungspunkte = new HashSet<>();
        Sitzung_File_Impl sitzung = new Sitzung_File_Impl(sitzungsdatum);

        String sitzungsbeginn = null;
        String sitzungsende = null;
        NodeList sitzungsteile = sitzungsverlauf.getChildNodes();
        for (int i = 0; i < sitzungsteile.getLength(); i++) {
            if (sitzungsteile.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element sitzungsteil = (Element) sitzungsteile.item(i);
            if (sitzungsteil.getNodeName().equals("sitzungsbeginn")) {
                sitzungsbeginn = sitzungsteil.getAttribute("sitzung-start-uhrzeit");
            }
            if (sitzungsteil.getNodeName().equals("sitzungsende")) {
                sitzungsende = sitzungsteil.getAttribute("sitzung-ende-uhrzeit");
            }
            if (!sitzungsteil.getNodeName().equals("sitzungsbeginn") && !sitzungsteil.getNodeName().equals("tagesordnungspunkt")) {
                continue;
            }

            // TODO: Tagesordnungspunkt-Bezeichungen lesen.

            NodeList redenList = sitzungsteil.getElementsByTagName("rede");
            for (int j = 0; j < redenList.getLength(); j++) {
                Element redeElement = (Element) redenList.item(j);
                String redeId = redeElement.getAttribute("id");

                Element rednerElement = (Element) redeElement.getElementsByTagName("redner").item(0);
                String rednerId = rednerElement.getAttribute("id");

                boolean isRedner = false;
                List<String> redeAusschnitte = new ArrayList<>();
                NodeList redeChildNodes = redeElement.getChildNodes();
                List<String> kommentare = new ArrayList<>();
                for (int k = 0; k < redeChildNodes.getLength(); k++) {

                    if (redeChildNodes.item(k).getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    Element childNode = (Element) redeChildNodes.item(k);

                    if (childNode.getNodeName().equals("kommentar")) {
                        kommentare.add(childNode.getFirstChild().getTextContent());
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

                if (rednerId.startsWith("9999") /* Reden von Gästen werden ignoriert */) {
                    continue;
                } else if (!abgeordnete.containsKey(rednerId)) {
                    throw new AbgeordneterNotFoundException("Abgeordnter mit ID '" + rednerId + "' wurde nicht gefunden");
                }
                Abgeordneter_File_Impl redner = abgeordnete.get(rednerId);

                Rede rede = new Rede_File_Impl(redeId, redner, String.join("\n", redeAusschnitte), kommentare, sitzungsdatum, sitzung);
                reden.add(rede);
                sitzung.addRede(rede);
                redner.addRede(rede);
            }
        }
        if (sitzungsbeginn == null || sitzungsende == null) {
            throw new RuntimeException("Sitzung hat keine Dauer :blobcatghostdead:");
        }
        List<Integer> beginnList = Arrays.stream(sitzungsbeginn.split(":")).map(Integer::parseInt).collect(Collectors.toList());
        List<Integer> endeList = Arrays.stream(sitzungsende.split(":")).map(Integer::parseInt).collect(Collectors.toList());

        int hours = endeList.get(0) - beginnList.get(0);
        int minutes = endeList.get(1) - beginnList.get(1);
        minutes += hours * 60;

        // Day rollover, lets just add 24 hours and pretend nothing happened.
        if (minutes < 0) {
            minutes += 60 * 24;
        }
        sitzung.setDauer(minutes);

        Tagesordnung tagesordnung = new Tagesordnung_File_Impl(sitzung, tagesordnungspunkte);
        sitzung.setTagesordnung(tagesordnung);
        sitzungen.add(sitzung);
    }

    /**
     * Lokale Hilfsfunktion, die einen Abgeordneten nach seinem Namen sucht.
     *
     * @param vorname  Vorname des Abgeordneten
     * @param nachname Nachname des Abgeordneten
     * @param nurAbc   Ob Umlaute wie z.B. ä/ö/ü verboten sein sollen
     * @return Den Abgeordneten. null, wenn keiner gefunden werden kann
     */
    private Abgeordneter_File_Impl getAbgeordneterByName(String vorname, String nachname, boolean nurAbc) {
        String nachnameFormatted = nachname.toLowerCase().replace("ğ", "g").replace("ć", "c").replace("ă", "a");
        String vornameFormatted = vorname.toLowerCase().replace("ğ", "g").replace("ć", "c").replace("ă", "a");
        if (nurAbc) {
            nachnameFormatted = nachnameFormatted.replaceAll("[^a-zA-Z ()]", "");
            vornameFormatted = vornameFormatted.replaceAll("[^a-zA-Z ()]", "");
        }

        Abgeordneter_File_Impl abgeordneter = null;
        for (Abgeordneter_File_Impl potentiellerAbgeordneter : abgeordnete.values()) {
            String potentiellerNachname = potentiellerAbgeordneter.getNachname();
            String potentiellerVorname = potentiellerAbgeordneter.getVorname();
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
        return abgeordneter;
    }

    /**
     * @return Menge aller Abgeordneter
     */
    @Override
    public Set<Abgeordneter> listAbgeordnete() {
        return new HashSet<>(abgeordnete.values());
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
     * Listet alle Sitzungen auf
     *
     * @return Liste aller Sitzungen
     */
    @Override
    public Set<Sitzung> listSitzungen() {
        return new HashSet<>(sitzungen);
    }

    /**
     * Listet alle Tagesordnungen auf
     *
     * @return Liste aller Tagesordnungen
     */
    @Override
    public Set<Tagesordnung> listTagesordnungen() {
        return sitzungen.stream().map(Sitzung::getTagesordnung).collect(Collectors.toSet());
    }
}
