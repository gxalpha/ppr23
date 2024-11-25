package org.texttechnologylab.project.Parliament_Browser_09_2.parsing;

import org.texttechnologylab.project.Parliament_Browser_09_2.data.Rede;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElement;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Sitzung;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Tagesordnungspunkt;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.RedeElementImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.RedeImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.SitzungImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.TagesordnungspunktImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Static class that parses protocolXMLs
 *
 * @author Stud
 */
public class ProtocolParser {


    /**
     * Method to parse an XML document and create a Sitzung Object.
     *
     * @param document a XML document
     * @return Sitzungs object
     * @throws ParserConfigurationException
     * @author Stud
     */
    public static Sitzung parseSitzung(Document document) throws ParserConfigurationException {

        int wahlperiode = 0;
        int sitzungsnr = 0;
        Date beginn;
        Date ende;
        List<Tagesordnungspunkt> tagesordnungspunkte = new ArrayList<>();


        // get Date
        Date date = null;

        NodeList dbtList = document.getElementsByTagName("dbtplenarprotokoll");
        for (int k = 0; k < dbtList.getLength(); k++) {
            Node dbtNode = dbtList.item(k);

            if (dbtNode.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element dbt = (Element) dbtNode;

                String date_string = dbt.getAttribute("sitzung-datum"); // parse to Time
                date = XMLTools.parseDate(date_string);
            }
        }

        beginn = getStartTime(document, date);
        ende = getEndTime(document, date);

        // get Sitzungsnummer und Wahlperiode

        NodeList kopfdaten = document.getElementsByTagName("kopfdaten");
        for (int i = 0; i < kopfdaten.getLength(); i++) {
            Node kopfdatum = kopfdaten.item(i);

            if (kopfdatum.getNodeType() == Node.ELEMENT_NODE) {
                Element kopfdatumElement = (Element) kopfdatum;

                // get Kopfdatumselement
                NodeList plenarprotokoll = kopfdatumElement.getElementsByTagName("plenarprotokoll-nummer");

                for (int j = 0; j < plenarprotokoll.getLength(); j++) {
                    Node plenarpNode = plenarprotokoll.item(j);

                    if (plenarpNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element plenarElement = (Element) plenarpNode;


                        // get Wahlperiode

                        String wp_string = plenarElement.getElementsByTagName("wahlperiode").item(0).getTextContent();
                        wahlperiode = Integer.parseInt(wp_string);


                        // get Sitzungsnummer

                        String number_string = plenarElement.getElementsByTagName("sitzungsnr").item(0).getTextContent();
                        // if number String ends with (neu) create substring without it
                        String numberOnly = number_string.replaceAll("[^0-9]", "");

                        sitzungsnr = Integer.parseInt(numberOnly);

                    }
                }

            }

        }


        // get Tagesordnungspunkte

        NodeList ivz = document.getElementsByTagName("inhaltsverzeichnis");

        for (int i = 0; i < ivz.getLength(); i++) {
            Node ivzNode = ivz.item(i);

            if (ivzNode.getNodeType() == Node.ELEMENT_NODE) {
                Element ivzElement = (Element) ivzNode;

                NodeList blockList = ivzElement.getElementsByTagName("ivz-block");

                // für jeden block create tagesordnungspunkt/zusatzpunkt

                for (int j = 0; j < blockList.getLength(); j++) {
                    Node blockNode = blockList.item(j);

                    if (blockNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element blockElement = (Element) blockNode;

                        Tagesordnungspunkt tagesordnungspunkt = parseAgendaItem(blockElement);

                        if (tagesordnungspunkt != null) {
                            tagesordnungspunkte.add(tagesordnungspunkt);
                        }

                    }

                }

            }

        }


        Sitzung sitzung = new SitzungImpl(wahlperiode, sitzungsnr, date, beginn, ende, tagesordnungspunkte);

        return sitzung;

    }


    /**
     * Method to get the start time of a Sitzung.
     *
     * @param document the XML document
     * @return localTime of Sitzingsstart.
     * @author Stud
     */
    public static Date getStartTime(Document document, Date date) {

        LocalTime start_time = null;

        NodeList dbtList = document.getElementsByTagName("dbtplenarprotokoll");
        for (int k = 0; k < dbtList.getLength(); k++) {
            Node dbtNode = dbtList.item(k);

            if (dbtNode.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element dbt = (Element) dbtNode;

                String start_string = dbt.getAttribute("sitzung-start-uhrzeit"); // parse to Time
                start_time = XMLTools.parseTime(start_string);
            }
        }

        return Date.from(start_time.atDate(date.toInstant().atOffset(ZoneOffset.UTC).toLocalDate()).toInstant(ZoneOffset.UTC));
    }


    /**
     * Method to get the end time of a Sitzung.
     *
     * @param document the xml document
     * @return localtime of Sitzungsende.
     * @author Stud
     */
    public static Date getEndTime(Document document, Date date) {

        LocalTime end_time = null;

        NodeList dbtList = document.getElementsByTagName("dbtplenarprotokoll");
        for (int k = 0; k < dbtList.getLength(); k++) {
            Node dbtNode = dbtList.item(k);

            if (dbtNode.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element dbt = (Element) dbtNode;

                String start_string = dbt.getAttribute("sitzung-ende-uhrzeit"); // parse to Time
                end_time = XMLTools.parseTime(start_string);
            }
        }

        return Date.from(end_time.atDate(date.toInstant().atOffset(ZoneOffset.UTC).toLocalDate()).toInstant(ZoneOffset.UTC));

    }


    /**
     * Method to parse an Agenda Item in a Sitzungsprotokoll.
     *
     * @param blockElement Element of a single Inhaltsverzeichnis entry
     * @return a new Tagesordnungsobject.
     * @author Stud
     */
    public static Tagesordnungspunkt parseAgendaItem(Element blockElement) { //blockElement

        List<String> redenIDs = new ArrayList<>();

        String titel = blockElement.getElementsByTagName("ivz-block-titel").item(0).getTextContent();
        if (titel.startsWith("Anlage")) {
            return null;
        }
        String topic = null;

        NodeList eintragList = blockElement.getElementsByTagName("ivz-eintrag");

        for (int i = 0; i < eintragList.getLength(); i++) {
            Node eintragNode = eintragList.item(i);

            if (eintragNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eintragElement = (Element) eintragNode;

                if (topic == null) {
                    topic = eintragElement.getElementsByTagName("ivz-eintrag-inhalt").item(0).getTextContent();
                }

                try {
                    Element redeXref = (Element) eintragElement.getElementsByTagName("xref").item(0);
                    if (redeXref.getAttribute("ref-type").equals("rede")) {
                        // Extract the id attribute
                        String redeId = redeXref.getAttribute("rid");
                        redenIDs.add(redeId);
                    }

                } catch (NullPointerException e) {
                }

            }

        }

        String thema = titel + " " + topic;

        Tagesordnungspunkt tagesordnungspunkt = new TagesordnungspunktImpl(thema, redenIDs);

        return tagesordnungspunkt;

    }


    /**
     * Creates a list of Rede w3c elements for every Rede in the document.
     *
     * @param document
     * @return set of Rede Element implicating every single Rede in the document.
     * @author Stud
     */
    public static List<Element> create_redeElemente(Document document) {

        List<Element> redeElement_list = new ArrayList<>();

        NodeList tlist = document.getElementsByTagName("tagesordnungspunkt");

        for (int j = 0; j < tlist.getLength(); j++) {
            Node tages = tlist.item(j);

            if (tages.getNodeType() == Node.ELEMENT_NODE) {
                Element tElement = (Element) tages;

                NodeList reden = tElement.getElementsByTagName("rede");

                for (int n = 0; n < reden.getLength(); n++) {
                    Node rede = reden.item(n);

                    if (rede.getNodeType() == Node.ELEMENT_NODE) {
                        Element redeElement = (Element) rede;

                        redeElement_list.add(redeElement);
                    }
                }
            }
        }
        return redeElement_list;
    }


    /**
     * Method to parse one Redeelement (Absätze, Kommentare,...) in a Rede.
     *
     * @param speechNode a Rede Element in a XML document.
     * @param datum      Date of speech
     * @return a new Redeobject
     * @author Stud
     */
    public static Rede parseRede(Element speechNode, Date datum) {

        String id = null;
        String rednerID = null;
        List<RedeElement> redeElemente = new ArrayList<>();


        id = speechNode.getAttribute("id");

        NodeList plist = speechNode.getElementsByTagName("p");

        for (int s = 0; s < plist.getLength(); s++) {
            Node pNode = plist.item(s);

            if (pNode.getNodeType() == Node.ELEMENT_NODE) {
                Element pElement = (Element) pNode;

                NodeList redner_list = pElement.getElementsByTagName("redner");

                for (int t = 0; t < redner_list.getLength(); t++) {
                    Node rednerNode = redner_list.item(t);

                    if (rednerNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element rednerElement = (Element) rednerNode;

                        rednerID = rednerElement.getAttribute("id");

                    }

                }

            }

        }

        // Redetext einlesen bzw. Liste von RedeElementen erstellen

        RedeElement.RedeElementTyp rednerTyp = RedeElement.RedeElementTyp.ABSATZ;

        NodeList elements = speechNode.getChildNodes();
        for (int j = 0; j < elements.getLength(); j++) {
            if (elements.item(j).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) elements.item(j); // j-ter "Teil" der Rede

                // Kommentare zählen nicht zur Rede und werden daher ignoriert
                if (element.getTagName().equals("kommentar")) {
                    String text = element.getTextContent();
                    redeElemente.add(new RedeElementImpl(RedeElement.RedeElementTyp.KOMMENTAR, text));
                }
                // Nach einem "name"-Tag redet i.d.R. der (Vize)-Präsident o.Ä., nicht der Redner
                // → folgenden Text nicht mit zur Rede nehmen, bis der Redner selbst wieder dran ist
                else if (element.getTagName().equals("name")) {
                    rednerTyp = RedeElement.RedeElementTyp.PRAESIDIUM;
                }
                // Zwischenfragen (wenn ein Redner zwischen der Rede aufgeführt wird) zählen nicht zu der Rede dazu
                // - Es sei denn, das Redner-Attribut ist der Redner selbst → dann wird die Rede fortgeführt:
                else if (element.getAttribute("klasse").equals("redner")) {
                    String zwischenrednerID = element.getElementsByTagName("redner").item(0).getAttributes().item(0).getTextContent();
                    if (zwischenrednerID.equals(rednerID)) {
                        rednerTyp = RedeElement.RedeElementTyp.ABSATZ;
                    } else {
                        rednerTyp = RedeElement.RedeElementTyp.ZWISCHENREDNER;
                    }
                }
                // Sonst p-Knoten, der Text enthält, einlesen
                else {
                    String neuerText = element.getTextContent();
                    RedeElement speech = new RedeElementImpl(rednerTyp, neuerText);
                    redeElemente.add(speech);
                }
            }
        }


        Rede rede = new RedeImpl(id, rednerID, datum, redeElemente);

        return rede;
    }


    /**
     * Method to parse all Reden in a protocol XML document.
     *
     * @param document the xml document
     * @return a list of Rede objects
     * @throws ParseException
     * @author Stud
     */
    public static List<Rede> parseAllRedenDocument(Document document) throws ParseException {

        Date datum = get_date(document);

        List<Element> redeElemente = create_redeElemente(document);
        List<Rede> list_reden = new ArrayList<>();

        for (Element redeElement : redeElemente) {

            Rede rede = parseRede(redeElement, datum);
            list_reden.add(rede);

        }

        return list_reden;

    }


    /**
     * Gets the date from the Speech.
     *
     * @param document the parsed xml file.
     * @return the Date in java.Date format.
     * @throws ParseException
     */
    public static Date get_date(Document document) throws ParseException {

        Date datum = null;

        NodeList kopfdaten = document.getElementsByTagName("kopfdaten");
        for (int i = 0; i < kopfdaten.getLength(); i++) {
            Node kopfdatum = kopfdaten.item(i);

            if (kopfdatum.getNodeType() == Node.ELEMENT_NODE) {
                Element kopfdatumElement = (Element) kopfdatum;

                NodeList vlist = kopfdatumElement.getElementsByTagName("veranstaltungsdaten");

                for (int k = 0; k < vlist.getLength(); k++) {
                    Node vNode = vlist.item(k);

                    if (vNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element vElement = (Element) vNode;

                        NodeList datelist = vElement.getElementsByTagName("datum");
                        String datum_string = null;

                        if (datelist.getLength() > 0) {
                            Element dateElement = (Element) datelist.item(0);
                            datum_string = dateElement.getAttribute("date");
                            datum = XMLTools.parseDate(datum_string);
                        }

                    }

                }

            }

        }

        return datum;

    }


}
