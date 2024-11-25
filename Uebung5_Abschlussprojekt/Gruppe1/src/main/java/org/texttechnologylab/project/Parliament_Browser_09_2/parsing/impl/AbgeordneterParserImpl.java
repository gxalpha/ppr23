package org.texttechnologylab.project.Parliament_Browser_09_2.parsing.impl;

import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.AbgeordneterImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.parsing.AbgeordneterParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Klasse, die den Abgeordneten-Parser implementiert
 *
 * @author Stud
 */
public class AbgeordneterParserImpl implements AbgeordneterParser {

    /**
     * Liste der geparsten Abgeordneten
     */
    Set<Abgeordneter> abgeordnete = new HashSet<>();

    /**
     * Parse die Stammdaten-XML
     *
     * @param stammdatenXML XML, die alle Stammdaten der Abgeordneten enthält
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @author Stud
     * @author Stud (Änderungen)
     */
    @Override
    public void parseStammdaten(InputStream stammdatenXML) throws ParserConfigurationException, IOException, SAXException {

        // Auslesen der Wurzel der XML
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); // Stop builder from looking for the DTD
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.parse(stammdatenXML);
        Element wurzelXML = document.getDocumentElement();

        // Alle Kinder der XML lesen
        NodeList abgeordneteListe = wurzelXML.getChildNodes();

        // Durch alle Kinder der XML iterieren
        for (int i = 0; i < abgeordneteListe.getLength(); i++) {
            Node abgeordneterNode = abgeordneteListe.item(i);

            // Wenn kein Element_Node oder Titel VERSION, dann ist es kein Abgeordneter
            if (abgeordneterNode.getNodeType() != Node.ELEMENT_NODE || abgeordneterNode.getNodeName().equals("VERSION")) {
                continue;
            }

            // Abgeordneten rausholen
            Element abgeordneterElement = (Element) abgeordneterNode;

            // ID auslesen
            String id = getContentWithTagName(abgeordneterElement, "ID");

            // Namensdaten auselesen
            Element namenElement = (Element) abgeordneterElement.getElementsByTagName("NAMEN").item(0);
            NodeList namenNodes = namenElement.getElementsByTagName("NAME");
            Element name = (Element) namenNodes.item(namenNodes.getLength() - 1);
            String nachname = getContentWithTagName(name, "NACHNAME");
            String vorname = getContentWithTagName(name, "VORNAME");
            String ortszusatz = getContentWithTagName(name, "ORTSZUSATZ");
            String adelssuffix = getContentWithTagName(name, "ADEL");
            String namenspraefix = getContentWithTagName(name, "PRAEFIX");
            String anrede = getContentWithTagName(name, "ANREDE_TITEL");
            String akadTitel = getContentWithTagName(name, "AKAD_TITEL");

            // Biographische Angaben auslesen
            Element biografischeAngaben = (Element) abgeordneterElement.getElementsByTagName("BIOGRAFISCHE_ANGABEN").item(0);

            String geburtsdatumString = getContentWithTagName(biografischeAngaben, "GEBURTSDATUM");
            Date geburtsdatum = parseDateFromString(geburtsdatumString);

            String geburtsort = getContentWithTagName(biografischeAngaben, "GEBURTSORT");

            String sterbedatumString = getContentWithTagName(biografischeAngaben, "STERBEDATUM");
            Date sterbedatum = parseDateFromString(sterbedatumString);

            String geschlecht = getContentWithTagName(biografischeAngaben, "GESCHLECHT");

            String religion = getContentWithTagName(biografischeAngaben, "RELIGION");
            String beruf = getContentWithTagName(biografischeAngaben, "BERUF");
            String vita = getContentWithTagName(biografischeAngaben, "VITA_KURZ");
            String partei = getContentWithTagName(biografischeAngaben, "PARTEI_KURZ");

            // Falls Abgeordneter parteilos, das vermerken
            if (partei.equals("Plos") || partei.isEmpty()) {
                partei = "Parteilos";
            }

            // Abspeichern der Mandate und Mitgliedschaften als Strings, da sonst nicht benötigt
            List<String> mandate = new ArrayList<>();
            List<List<String>> mitgliedschaften = new ArrayList<>();

            String fraktion = null;

            // Wahlperioden des Abgeordneten auslesen
            NodeList wahlperiodenNode = ((Element) abgeordneterElement.getElementsByTagName("WAHLPERIODEN").item(0)).getElementsByTagName("WAHLPERIODE");

            // Abspeichern, ob ab Wahlperiode 19 aktiv
            boolean wpGroesser18 = false;

            // über alle Wahlperioden iterieren
            for (int j = 0; j < wahlperiodenNode.getLength(); j++) {
                Element wahlperiodeElement = (Element) wahlperiodenNode.item(j);

                String startDate = getContentWithTagName(wahlperiodeElement, "MDBWP_VON");
                String endDate = getContentWithTagName(wahlperiodeElement, "MDBWP_BIS");
                String mandatsart = getContentWithTagName(wahlperiodeElement, "MANDATSART");
                if (mandatsart.isEmpty()) {
                    mandatsart = "Unbekannte Mandatsart";
                }
                String wpNum = getContentWithTagName(wahlperiodeElement, "WP");
                int wpNumInt = Integer.parseInt(wpNum);

                // wenn Wahlperiode größer 18, dann vermerken, dass ab WP 19 aktiv
                if (wpNumInt > 18) {
                    wpGroesser18 = true;
                }

                mandate.add("Wahlperiode " + wpNum + ", " + startDate + "-" + endDate + " (" + mandatsart + ")");

                List<String> wpMitgliedschaften = new ArrayList<>();
                wpMitgliedschaften.add("WP " + wpNum);


                // Inhalte zu Institutionen auslesen
                NodeList institutionenList = ((Element) wahlperiodeElement.getElementsByTagName("INSTITUTIONEN").item(0)).getElementsByTagName("INSTITUTION");
                for (int k = 0; k < institutionenList.getLength(); k++) {
                    Element institution = (Element) institutionenList.item(k);
                    String institutionsArt = getContentWithTagName(institution, "INSART_LANG");
                    String institutionsName = getContentWithTagName(institution, "INS_LANG");
                    String funktion = getContentWithTagName(institution, "FKT_LANG");

                    switch (institutionsArt) {
                        case "Fraktion/Gruppe":
                            // Assume last to be parsed to be the latest one
                            fraktion = institutionsName;
                        case "Ausschuss":
                        case "Unterausschuss":
                        case "Untersuchungsausschuss":
                        case "Parlamentariergruppen":
                        case "Deutscher Bundestag":
                        case "Sonstiges Gremium":
                        case "Ministerium":
                        case "Enquete-Kommission":
                            if (funktion.isEmpty()) {
                                wpMitgliedschaften.add(institutionsName);
                            } else {
                                wpMitgliedschaften.add(institutionsName + " (" + funktion + ")");
                            }
                            break;
                    }
                }
                mitgliedschaften.add(wpMitgliedschaften);
            }

            // neuen Abgeordneten erstellen
            Abgeordneter abgeordneter = new AbgeordneterImpl(id, nachname, vorname, ortszusatz, adelssuffix, namenspraefix, anrede, akadTitel, geburtsdatum, geburtsort, sterbedatum, geschlecht, religion, beruf, vita, partei, fraktion, mandate, mitgliedschaften);

            // Abgeordneten nur abspeichern, wenn mindestens in WP 19 aktiv
            if (wpGroesser18) {
                abgeordnete.add(abgeordneter);
            }
        }


    }

    /**
     * @return Liste der geparsten Abgeordneten
     * @author Stud
     */
    @Override
    public Set<Abgeordneter> getAbgeordnete() {
        return abgeordnete;
    }

    /**
     * Hilfsmethode zum Parsen des Datums
     *
     * @param dateString Datumsstring der Datum als TT.MM.JJJJ enthält
     * @return String als SQL-Datum
     * @author Stud
     */
    @Override
    public Date parseDateFromString(String dateString) {
        if (dateString.isEmpty()) {
            return null;
        }

        // String des Datums umwandeln in ein Date-Objekt
        String jahr = dateString.substring(6, 10);
        String monat = dateString.substring(3, 5);
        String tag = dateString.substring(0, 2);
        // für Date-Objekt erwartetes Stringformat yyyy-mm-dd
        String dateStringFormat = jahr + "-" + monat + "-" + tag;
        Date date = Date.valueOf(dateStringFormat);

        return date;
    }

    /**
     * Hilfsfunktion, um Inhalt des Elements zu erhalten
     *
     * @param element Element der XML Datei
     * @param tagName Tag, nach welchem im Element gesucht werden soll
     * @return String, welcher den Inhalt enthält
     */
    @Override
    public String getContentWithTagName(Element element, String tagName) {
        return element.getElementsByTagName(tagName).item(0).getTextContent();
    }
}
