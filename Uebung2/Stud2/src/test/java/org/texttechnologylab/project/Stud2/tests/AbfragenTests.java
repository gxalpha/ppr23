package org.texttechnologylab.project.Stud2.tests;

import jxl.read.biff.BiffException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Abstimmung;
import org.texttechnologylab.project.data.Ausschuss;
import org.texttechnologylab.project.data.Fraktion;
import org.texttechnologylab.project.data.Mandat;
import org.texttechnologylab.project.data.Mitgliedschaft;
import org.texttechnologylab.project.data.Partei;
import org.texttechnologylab.project.data.Rede;
import org.texttechnologylab.project.data.Types;
import org.texttechnologylab.project.data.Wahlperiode;
import org.texttechnologylab.project.exception.BundestagException;
import org.texttechnologylab.project.Stud2.impl.exception.AttributeNotFoundError;
import org.texttechnologylab.project.Stud2.impl.helper.BundestagFactoryImpl;
import org.texttechnologylab.project.Stud2.impl.helper.Tuple;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Enthält alle Tests, die die Abfragen aus Aufgabe 3 umsetzen
 *
 * @author Stud2
 */
public class AbfragenTests {
    /**
     * @param path der Pfad der auszugebenden Textdatei
     */
    public void output(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();

            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
        }
    }

    /**
     * Bestimmt die Liste des Mandates, sofern vorhanden
     *
     * @param mandat das Mandat, aus dem die Landesliste bestimmt werden soll
     * @return die Liste des Mandates
     */
    public String getListe(Mandat mandat) throws AttributeNotFoundError {

        // Liste aus dem Label des Mandates extrahieren
        String label = mandat.getLabel();
        int cutOffAt = label.indexOf(".");

        String liste = "";
        if (cutOffAt != -1)
        {
            liste = label.substring(0 ,cutOffAt);
        }
        if (!liste.isEmpty()) {
            return liste;
        }
        throw new AttributeNotFoundError("Liste des Mandates konnte nicht gefunden werden.");

    }

    /**
     * Abfrage 3a) nach Fraktion - für Details siehe erzeugte .txt, Aufgabenstellung oder README
     */
    @Test
    public void abfrage3aNachFraktion() throws BiffException, IOException, ParserConfigurationException, ParseException, SAXException, BundestagException, InvalidFormatException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "src/main/resources/Bundestagsreden20",
                "");

        // Abgeordnete auf ihre ANZAHL der Reden und deren DURCHSCHNITTLICHE Länge abbilden
        List<Rede> alleReden = factory.listReden();
        alleReden.sort(Comparator.comparing(Rede::getRedner));

        Map<Abgeordneter, Tuple<Integer, Integer>> abgeordneterQuantityLength = new HashMap<>();

        assert !alleReden.isEmpty();
        Abgeordneter abgeordneter = alleReden.get(0).getRedner();
        int currQuantity = 0;
        int sumLengths = alleReden.get(0).getText().length();

        for (Rede r : alleReden) {
            if (r.getRedner().getID().equals(abgeordneter.getID())) {
                sumLengths += r.getText().length();
                currQuantity += 1;
            } else {
                abgeordneterQuantityLength.put(abgeordneter, new Tuple<>(currQuantity, sumLengths / currQuantity));
                currQuantity = 1;
                sumLengths = r.getText().length();
                abgeordneter = r.getRedner();
            }
        }

        Map<Abgeordneter, Tuple<Integer, Integer>> abgeordneterQuantityLengthCopy = new HashMap<>(abgeordneterQuantityLength);

        // Abgeordnete sortieren → Primär nach #Reden und sekundär nach #Durchschnittliche Zeichen pro Rede.
        List<Abgeordneter> abgeordneteSorted = new ArrayList<>();
        int currAvgLength = 0;
        currQuantity = 0;

        while (!abgeordneterQuantityLength.isEmpty()) {
            for (Abgeordneter a : abgeordneterQuantityLength.keySet()) {
                if (abgeordneterQuantityLength.get(a).getFirst() > currQuantity) {
                    currQuantity = abgeordneterQuantityLength.get(a).getFirst();
                    currAvgLength = abgeordneterQuantityLength.get(a).getSecond();
                    abgeordneter = a;
                } else if (abgeordneterQuantityLength.get(a).getFirst() == currQuantity) {
                    if (abgeordneterQuantityLength.get(a).getSecond() > currAvgLength) {
                        currQuantity = abgeordneterQuantityLength.get(a).getFirst();
                        currAvgLength = abgeordneterQuantityLength.get(a).getSecond();
                        abgeordneter = a;

                    }
                }
            }
            abgeordneteSorted.add(abgeordneter);
            abgeordneterQuantityLength.remove(abgeordneter);
            if (!abgeordneterQuantityLength.isEmpty()) {
                abgeordneter = null;
                currQuantity = 0;
                currAvgLength = 0;
            }
        }

        // Ausgabe der Ergebnisse
        FileWriter file = new FileWriter("Antworten/Abfrage 3a (nach Fraktion).txt");
        BufferedWriter writer = new BufferedWriter(file);

        int maxLengthName = 0;
        for (Abgeordneter mdb : abgeordneteSorted) {
            if ((mdb.getVorname() + " " + mdb.getName()).length() > maxLengthName) {
                maxLengthName = (mdb.getVorname() + " " + mdb.getName()).length();
            }
        }

        writer.write("\n\n" + "*".repeat(100));
        writer.write("\n" + "ALLE ABGEORDNETEN MIT REDEN IM BUNDESTAG (GEORDNET NACH FRAKTION > ANZAHL > DURCHSCHNITTLICHE LÄNGE)");
        writer.write("\n" + "*".repeat(100));
        for (Fraktion fraktion : factory.listFraktionen()) {
            // Wir listen nur Fraktionen auf, in denen es auch Redner gab.
            boolean redeVorhanden = false;
            for (Abgeordneter a : fraktion.getMembers()) {
                if (abgeordneteSorted.contains(a)) {
                    redeVorhanden = true;
                    break;
                }
            }
            if (redeVorhanden) {
                writer.write("\n" + "-".repeat(100));
                writer.write("\n" + fraktion.getLabel().toUpperCase());
                writer.write("\n" + "-".repeat(100));
                for (Abgeordneter mdb : abgeordneteSorted) {
                    if (fraktion.getMembers().contains(mdb)) {
                        writer.write("\n" + "ID" + mdb.getID() + " - " + mdb.getVorname() + " " + mdb.getName()
                                + " ".repeat(maxLengthName - (mdb.getVorname() + " " + mdb.getName()).length())
                                + " hat " + abgeordneterQuantityLengthCopy.get(mdb).getFirst() + " Reden gehalten "
                                + "(Durchschnitt: " + abgeordneterQuantityLengthCopy.get(mdb).getSecond() + " Zeichen)");
                    }
                }
            }
        }
        writer.close();

        // Ausgabe aus der Konsole
        output("Antworten/Abfrage 3a (nach Fraktion).txt");
    }

    /**
     * Abfrage 3a) nach Partei - für Details siehe erzeugte .txt, Aufgabenstellung oder README
     */
    @Test
    public void abfrage3aNachPartei() throws BiffException, IOException, ParserConfigurationException, ParseException, SAXException, BundestagException, InvalidFormatException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "src/main/resources/Bundestagsreden20",
                "");

        // Abgeordnete auf ihre ANZAHL der Reden und deren DURCHSCHNITTLICHE Länge abbilden
        List<Rede> alleReden = factory.listReden();
        alleReden.sort(Comparator.comparing(Rede::getRedner));

        Map<Abgeordneter, Tuple<Integer, Integer>> abgeordneterQuantityLength = new HashMap<>();

        assert !alleReden.isEmpty();
        Abgeordneter abgeordneter = alleReden.get(0).getRedner();
        int currQuantity = 0;
        int sumLengths = alleReden.get(0).getText().length();

        for (Rede r : alleReden) {
            if (r.getRedner().getID().equals(abgeordneter.getID())) {
                sumLengths += r.getText().length();
                currQuantity += 1;
            } else {
                abgeordneterQuantityLength.put(abgeordneter, new Tuple<>(currQuantity, sumLengths / currQuantity));
                currQuantity = 1;
                sumLengths = r.getText().length();
                abgeordneter = r.getRedner();
            }
        }

        Map<Abgeordneter, Tuple<Integer, Integer>> abgeordneterQuantityLengthCopy = new HashMap<>(abgeordneterQuantityLength);

        // Abgeordnete sortieren → Primär nach #Reden und sekundär nach #Durchschnittliche Zeichen pro Rede.
        List<Abgeordneter> abgeordneteSorted = new ArrayList<>();
        int currAvgLength = 0;
        currQuantity = 0;

        while (!abgeordneterQuantityLength.isEmpty()) {
            for (Abgeordneter a : abgeordneterQuantityLength.keySet()) {
                if (abgeordneterQuantityLength.get(a).getFirst() > currQuantity) {
                    currQuantity = abgeordneterQuantityLength.get(a).getFirst();
                    currAvgLength = abgeordneterQuantityLength.get(a).getSecond();
                    abgeordneter = a;
                } else if (abgeordneterQuantityLength.get(a).getFirst() == currQuantity) {
                    if (abgeordneterQuantityLength.get(a).getSecond() > currAvgLength) {
                        currQuantity = abgeordneterQuantityLength.get(a).getFirst();
                        currAvgLength = abgeordneterQuantityLength.get(a).getSecond();
                        abgeordneter = a;

                    }
                }
            }
            abgeordneteSorted.add(abgeordneter);
            abgeordneterQuantityLength.remove(abgeordneter);
            if (!abgeordneterQuantityLength.isEmpty()) {
                abgeordneter = null;
                currQuantity = 0;
                currAvgLength = 0;
            }
        }

        // Ausgabe der Ergebnisse
        FileWriter file = new FileWriter("Antworten/Abfrage 3a (nach Partei).txt");
        BufferedWriter writer = new BufferedWriter(file);

        int maxLengthName = 0;
        for (Abgeordneter mdb : abgeordneteSorted) {
            if ((mdb.getVorname() + " " + mdb.getName()).length() > maxLengthName) {
                maxLengthName = (mdb.getVorname() + " " + mdb.getName()).length();
            }
        }

        // Alle vorhandenen Parteien erfassen:
        Set<Partei> parteien = new HashSet<>();

        for (Abgeordneter mdb : abgeordneteSorted) {
            parteien.add(mdb.getPartei());
        }

        writer.write("\n\n" + "*".repeat(100));
        writer.write("\n" + "ALLE ABGEORDNETEN MIT REDEN IM BUNDESTAG (GEORDNET NACH PARTEI > ANZAHL > DURCHSCHNITTLICHE LÄNGE)");
        writer.write("\n" + "*".repeat(100));
        for (Partei partei : parteien) {
            writer.write("\n" + "-".repeat(100));
            writer.write("\n" + partei.getLabel().toUpperCase());
            writer.write("\n" + "-".repeat(100));
            for (Abgeordneter mdb : abgeordneteSorted) {
                if (partei.getMembers().contains(mdb)) {
                    writer.write("\n" + "ID" + mdb.getID() + " - " + mdb.getVorname() + " " + mdb.getName()
                            + " ".repeat(maxLengthName - (mdb.getVorname() + " " + mdb.getName()).length())
                            + " hat " + abgeordneterQuantityLengthCopy.get(mdb).getFirst() + " Reden gehalten "
                            + "(Durchschnitt: " + abgeordneterQuantityLengthCopy.get(mdb).getSecond() + " Zeichen)");
                }
            }
        }
        writer.close();

        // Ausgabe auf der Konsole
        output("Antworten/Abfrage 3a (nach Partei).txt");
    }

    // ACHTUNG: In der Aufgabenstellung 3a) ist auch nach "so wie dem Fehlen dieser" (also Parteilose oder Fraktionslose)
    //          gefragt. Diese sind jeweils in den beiden vorherigen Abfragen aufgeführt.

    /**
     * Abfrage 3b) - für Details siehe erzeugte .txt, Aufgabenstellung oder README
     */
    @Test
    public void abfrage3b() throws BiffException, IOException, ParserConfigurationException, ParseException, SAXException, BundestagException, InvalidFormatException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "",
                "");

        // Mitglieder aller Ausschüsse bestimmen (über die gesamte Historie hinweg)
        Set<Mitgliedschaft> alleMitgliedschaften = factory.listMitgliedschaften();

        List<Ausschuss> alleAusschuesse = new ArrayList<>();

        for (Mitgliedschaft mitgliedschaft : alleMitgliedschaften) {
            if (mitgliedschaft.getGruppe() instanceof Ausschuss) {
                Ausschuss ausschuss = (Ausschuss) mitgliedschaft.getGruppe();
                boolean ausschuss_exists = false;

                for (Ausschuss a: alleAusschuesse) {
                    if (ausschuss.getLabel().equals(a.getLabel())){
                        ausschuss_exists = true;
                        break;
                    }
                }
                if (!ausschuss_exists) {
                    alleAusschuesse.add(ausschuss);
                }
            }
        }

        alleAusschuesse.sort(Comparator.comparing(o -> o.getMembers().size()));
        Collections.reverse(alleAusschuesse);

        // Ausgabe der Ergebnisse
        FileWriter file = new FileWriter("Antworten/Abfrage 3b.txt");
        BufferedWriter writer = new BufferedWriter(file);

        int maxLengthName = 0;
        for (Mitgliedschaft mdb : alleMitgliedschaften) {
            if ((mdb.getAbgeordneter().getVorname() + " " + mdb.getAbgeordneter().getName()).length() > maxLengthName) {
                maxLengthName = (mdb.getAbgeordneter().getVorname() + " " + mdb.getAbgeordneter().getName()).length();
            }
        }
        writer.write("\n\n" + "*".repeat(140));
        writer.write("\nAUSSCHÜSSE IM BUNDESTAG - ABSTEIGEND SORTIERT NACH DER ANZAHL ALLER BISHERIGEN MITGLIEDER");
        writer.write("\n" + "*".repeat(140));

        for (Ausschuss ausschuss : alleAusschuesse) {
            writer.write("\n" + "-".repeat(140));
            writer.write("\n" + ausschuss.getLabel().toUpperCase() + " [Anzahl aller Mitglieder: " + ausschuss.getMembers().size() + " (inkl. ehemalig)]");
            writer.write("\n" + "-".repeat(140));
            for (Abgeordneter mdb : ausschuss.getMembers()) {
                Fraktion fraktion = null;
                for (Mitgliedschaft mitgliedschaft : mdb.listMitgliedschaften()) {
                    if (mitgliedschaft.getGruppe() instanceof Fraktion) {
                        fraktion = (Fraktion) mitgliedschaft.getGruppe();
                    }
                }
                assert fraktion != null;
                writer.write("\n" + "ID" + mdb.getID() + " - " + mdb.getVorname() + " " + mdb.getName() +
                        " ".repeat(maxLengthName - (mdb.getVorname() + " " + mdb.getName()).length())  + "[" + fraktion.getLabel() + "]");
            }
            writer.write(ausschuss.getMembers().size());
        }
        writer.close();
        // Ausgabe auf der Konsole
        output("Antworten/Abfrage 3b.txt");
    }

    /**
     * Abfrage 3c) nach Fraktion - für Details siehe erzeugte .txt, Aufgabenstellung oder README
     */
    @Test
    public void abfrage3cNachFraktion() throws BiffException, IOException, ParserConfigurationException, ParseException, SAXException, BundestagException, InvalidFormatException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "",
                "");

        FileWriter file = new FileWriter("Antworten/Abfrage 3c (nach Fraktion).txt");
        BufferedWriter writer = new BufferedWriter(file);

        writer.write("\n\n" + "*".repeat(100));
        writer.write("\nABGEORDNETE MIT MITGLIEDSCHAFTEN IN AUSSCHÜSSEN - GEORDNET NACH FRAKTIONEN");
        writer.write("\n" + "*".repeat(100));
        writer.write("\n\n -- HINWEIS: Abgeordnete ohne Mitgliedschaften in einem Ausschuss sind ganz unten aufgeführt. -- \n");
        writer.write  ("\n -- HINWEIS: Fraktionen ohne Abgeordnete in irgendeinem Ausschuss werden nicht aufgeführt.    -- ");

        Set<Abgeordneter> alleAbgeordneten = factory.listAbgeordnete();

        // Mitgliedschaften in Ausschüssen nach Fraktion finden
        for (Fraktion fraktion : factory.listFraktionen()) {

            boolean erstesMitgliedDerFraktionImAusschuss = true;
            for (Abgeordneter abgeordneter : fraktion.getMembers()) {

                Map<Ausschuss, Integer> ausschussAnzahl = new HashMap<>();

                for (Mitgliedschaft mitgliedschaft : abgeordneter.listMitgliedschaften()) {
                    if (mitgliedschaft.getGruppe() instanceof Ausschuss) {
                        Ausschuss ausschuss = (Ausschuss) mitgliedschaft.getGruppe();

                        if (!ausschussAnzahl.containsKey(ausschuss)) {
                            ausschussAnzahl.put(ausschuss, 1);
                        }
                        else {
                            ausschussAnzahl.put(ausschuss, ausschussAnzahl.get(ausschuss) + 1);
                        }
                        alleAbgeordneten.remove(abgeordneter);
                    }
                }
                // Nur diejenigen Abgeordneten auflisten, bei denen eine Mitgliedschaft in einem Ausschuss zu verzeichnen ist
                if (!ausschussAnzahl.isEmpty()) {
                    if (erstesMitgliedDerFraktionImAusschuss) {
                        writer.write("\n\n" + "-".repeat(100));
                        writer.write("\n" + fraktion.getLabel().toUpperCase());
                        writer.write("\n" + "-".repeat(100));
                        erstesMitgliedDerFraktionImAusschuss = false;
                    }
                    writer.write("\n\nID" + abgeordneter.getID() + " - " + abgeordneter.getVorname() + " " + abgeordneter.getName());
                    writer.write("\nMITGLIEDSCHAFTEN in Ausschüssen über alle Wahlperioden: ");
                    for (Ausschuss a : ausschussAnzahl.keySet()) {
                        writer.write("\n - " + a.getLabel() + " (" + ausschussAnzahl.get(a) + " mal)");
                    }
                }
            }
        }
        writer.write("\n" + "-".repeat(100));
        writer.write("\nABGEORDNETE, DIE KEINE MITGLIEDSCHAFT IN EINEM AUSSCHUSS ZU VERZEICHNEN HABEN");
        writer.write("\n" + "-".repeat(100));
        for (Abgeordneter a : alleAbgeordneten) {
            writer.write("\n" + "ID" + a.getID() + " - " + a.getVorname() + " " + a.getName());
        }
        writer.close();
        // Ausgabe auf der Konsole
        output("Antworten/Abfrage 3c (nach Fraktion).txt");
    }

    /**
     * Abfrage 3c) nach Wahlperioden - für Details siehe erzeugte .txt, Aufgabenstellung oder README
     */
    @Test
    public void abfrage3cNachWahlperiode() throws BiffException, IOException, ParserConfigurationException, ParseException, SAXException, BundestagException, InvalidFormatException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "",
                "");

        FileWriter file = new FileWriter("Antworten/Abfrage 3c (nach Wahlperiode).txt");
        BufferedWriter writer = new BufferedWriter(file);

        writer.write("\n" + " ");
        writer.write("\n" + "*".repeat(120));
        writer.write("\nABGEORDNETE MIT MITGLIEDSCHAFTEN IN AUSSCHÜSSEN - GEORDNET NACH WAHLPERIODEN");
        writer.write("\n" + "*".repeat(120));
        writer.write("\n\n -- HINWEIS: Abgeordnete ohne Mitgliedschaften in einem Ausschuss sind ganz unten aufgeführt.  -- \n");
        writer.write  ("\n -- HINWEIS: WP ohne verzeichnete Mitgliedschaften in einem Ausschuss werden nicht aufgeführt. -- \n");

        Set<Abgeordneter> alleAbgeordneten = factory.listAbgeordnete();

        // Enthält die Abgeordneten ohne Mitgliedschaften in einem Ausschuss
        Set<Abgeordneter> alleAbgeordnetenCopy = new HashSet<>(alleAbgeordneten);

        // Mitgliedschaften in Ausschüssen nach Wahlperiode finden
        for (Wahlperiode wp : factory.getWahlperioden()) {

            boolean ersterAbgeordneterDerWP = true;

            for (Abgeordneter abgeordneter : factory.listAbgeordnete()) {

                Set<Mitgliedschaft> mitgliedschaftenAusschuesse = new HashSet<>();

                for (Mitgliedschaft mitgliedschaft : abgeordneter.listMitgliedschaften(wp)) {
                    if (mitgliedschaft.getGruppe() instanceof Ausschuss) {
                        mitgliedschaftenAusschuesse.add(mitgliedschaft);
                    }
                }

                if (!mitgliedschaftenAusschuesse.isEmpty()) {
                    if (ersterAbgeordneterDerWP) {
                        writer.write("\n\n" + "-".repeat(120));
                        writer.write("\nWAHLPERIODE: " + wp.getNumber());
                        writer.write("\n" + "-".repeat(120));
                        ersterAbgeordneterDerWP = false;
                    }
                    writer.write("\n\nID" + abgeordneter.getID() + " - " + abgeordneter.getVorname() + " " + abgeordneter.getName());
                    writer.write("\nMITGLIEDSCHAFTEN in Ausschüssen in WP" + wp.getNumber() + " (Insgesamt: " + mitgliedschaftenAusschuesse.size() + ")");

                    // Abgeordneter hat eine Mitgliedschaft in einem Ausschuss:
                    alleAbgeordnetenCopy.remove(abgeordneter);

                    for (Mitgliedschaft m : mitgliedschaftenAusschuesse) {
                        writer.write("\n - " + m.getGruppe().getLabel() + " [" + m.getFunktion() + "]");
                    }
                }
            }
        }

        writer.write("\n\n" + "-".repeat(100));
        writer.write("\nABGEORDNETE, DIE KEINE MITGLIEDSCHAFT IN EINEM AUSSCHUSS ZU VERZEICHNEN HABEN");
        writer.write("\n" + "-".repeat(100));
        for (Abgeordneter a : alleAbgeordnetenCopy) {
            writer.write("\n" + "ID" + a.getID() + " - " + a.getVorname() + " " + a.getName());
        }
        writer.close();
        // Ausgabe auf der Konsole
        output("Antworten/Abfrage 3c (nach Wahlperiode).txt");
    }

    /**
     * Abfrage 3d) - für Details siehe erzeugte .txt, Aufgabenstellung oder README
     */
    @Test
    public void abfrage3d() throws BiffException, IOException, ParserConfigurationException, ParseException, SAXException, BundestagException, InvalidFormatException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "",
                "");

        FileWriter file = new FileWriter("Antworten/Abfrage 3d.txt");
        BufferedWriter writer = new BufferedWriter(file);

        writer.write("\n\n" + "*".repeat(120));
        writer.write("\nABGEORDNETE MIT FÜHRUNGSPOSITIONEN IN AUSSCHÜSSEN (Obmann/Obfrau/Stellvertreter) - ABSTEIGEND SORTIERT NACH HÄUFIGKEIT");
        writer.write("\n" + "*".repeat(120));
        writer.write("\n\n -- HINWEIS: Abgeordnete ohne Führungspositionen in einem Ausschuss sind ganz unten aufgeführt. -- \n");

        Set<Abgeordneter> alleAbgeordneten = factory.listAbgeordnete();

        // Enthält die Abgeordneten ohne Führungspositionen (Obmann/Obfrau/Stellvertretendes Mitglied) in einem Ausschuss
        Set<Abgeordneter> alleAbgeordnetenCopy = new HashSet<>(alleAbgeordneten);

        List<Set<Mitgliedschaft>> leitendeAbgeordnete = new ArrayList<>();

        for (Abgeordneter abgeordneter : factory.listAbgeordnete()) {

            Set<Mitgliedschaft> mitgliedschaftenAusschuesse = new HashSet<>();

            for (Mitgliedschaft mitgliedschaft : abgeordneter.listMitgliedschaften()) {
                if (mitgliedschaft.getGruppe() instanceof Ausschuss &&
                        ((mitgliedschaft.getFunktion().equals("Obmann")) ||
                        (mitgliedschaft.getFunktion().equals("Obfrau")) ||
                        (mitgliedschaft.getFunktion().equals("Stellvertretendes Mitglied")))) {
                    mitgliedschaftenAusschuesse.add(mitgliedschaft);
                }
            }

            if (!mitgliedschaftenAusschuesse.isEmpty()) {
                leitendeAbgeordnete.add(mitgliedschaftenAusschuesse);

                // Abgeordneter hat eine Mitgliedschaft in einem Ausschuss:
                alleAbgeordnetenCopy.remove(abgeordneter);
            }
        }

        // Ausgabe der Ergebnisse
        leitendeAbgeordnete.sort(Comparator.comparing(Set::size));
        Collections.reverse(leitendeAbgeordnete);

        for (Set<Mitgliedschaft> leitung : leitendeAbgeordnete) {
            ArrayList<Mitgliedschaft> leitungArray = new ArrayList<>(leitung);
            Abgeordneter abgeordneter = leitungArray.get(0).getAbgeordneter();
            writer.write("\n" + "-".repeat(120));
            writer.write("\nID" + abgeordneter.getID() + " - " + abgeordneter.getVorname() + " " + abgeordneter.getName());
            writer.write("\nINSGESAMT " + leitung.size() + " FÜHRUNGSPOSITIONEN in Ausschüssen:");

            for (Mitgliedschaft m : leitung) {
                writer.write("\n - " + m.getGruppe().getLabel() + " [" + m.getFunktion() + " in WP" + m.getWahlperiode().getNumber() + "]");
            }
        }

        writer.write("\n\n" + "-".repeat(120));
        writer.write("\nABGEORDNETE, DIE KEINE FÜHRUNGSPOSITION IN EINEM AUSSCHUSS ZU VERZEICHNEN HABEN");
        writer.write("\n" + "-".repeat(120));
        for (Abgeordneter a : alleAbgeordnetenCopy) {
            writer.write("\n" + "ID" + a.getID() + " - " + a.getVorname() + " " + a.getName());
        }
        writer.close();
        // Ausgabe auf der Konsole
        output("Antworten/Abfrage 3d.txt");
    }

    /**
     * Abfrage 3d) (Oppositionsführer jeder Wahlperiode) - für Details siehe erzeugte .txt, Aufgabenstellung oder README
     */
    @Test
    public void abfrage3dOpposition() throws BiffException, IOException, ParserConfigurationException, ParseException, SAXException, BundestagException, InvalidFormatException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "",
                "");

        FileWriter file = new FileWriter("Antworten/Abfrage 3d (Oppositionsfuehrer).txt");
        BufferedWriter writer = new BufferedWriter(file);

        writer.write("\n\n" + "*".repeat(100));
        writer.write("\nOPPOSITIONSFÜHRER JEDER WAHLPERIODE");
        writer.write("\n" + "*".repeat(100));
        writer.write("\n\n -- HINWEIS: Oppositionsführer aus Wahlperioden ohne erfasste Minister können nicht " +
                           "\n             mit den gegebenen Daten erfasst werden und werden daher nicht aufgeführt. -- \n");

        // Liste aller Ministerarten
        List<String> minister = Arrays.asList(
                "Staatsministerin",
                "Staatsminister",
                "Bundeskanzler",
                "Bundeskanzlerin",
                "Bundesministerin",
                "Bundesminister");

        // Für jede Wahlperiode die Opposition bestimmen, falls Ministereinträge vorhanden
        // Falls in einer Wahlperiode keine Minister erfasst wurden, so kann kein Oppositionsführer bestimmt werden
        for (Wahlperiode wahlperiode : factory.getWahlperioden()) {
            boolean keineMinister = true;

            // Fraktionen dieser Wahlperiode erfassen
            Set<Fraktion> fraktionen = new HashSet<>();

            for (Mandat mandat : wahlperiode.listMandate()) {
                fraktionen.addAll(mandat.getFraktionen());
            }

            List<Fraktion> oppositionsFraktionen = new ArrayList<>(fraktionen);
            Set<Fraktion> regierung = new HashSet<>();

            for (Mitgliedschaft mitgliedschaft : factory.listMitgliedschaften()) {
                if (minister.contains(mitgliedschaft.getFunktion()) &&
                        mitgliedschaft.getWahlperiode().getNumber() == wahlperiode.getNumber()) {
                    keineMinister = false;

                    // Fraktion des Abgeordneten zu dieser Wahlperiode ermitteln und ...
                    for (Mandat mandat : mitgliedschaft.getAbgeordneter().listMandate()) {
                        for (Fraktion fraktion : mandat.getFraktionen()) {
                            // ... aus der Liste der Oppositionen entfernen, da Oppositionen keine Minister stellen können
                            oppositionsFraktionen.removeIf(fraktion2 -> fraktion2.getLabel().equals(fraktion.getLabel()));
                            // ... sowie in die Liste der regierenden Fraktionen hinzufügen.
                            regierung.add(fraktion);
                        }
                    }
                }
            }

            // Ausgabe der Ergebnisse
            if (!keineMinister) {
                writer.write("\n" + "*".repeat(100));
                writer.write("\nWAHLPERIODE: " + wahlperiode.getNumber());
                writer.write("\n" + "*".repeat(100));
                writer.write("\nFRAKTIONEN dieser Wahlperiode (Insgesamt: " + fraktionen.size() + ")");
                for (Fraktion f : fraktionen) {
                    writer.write("\n"+ " - " + f.getLabel());
                }

                // Regierende Parteien auflisten
                writer.write("\n" + "-".repeat(100));
                writer.write("\nDie Regierung: ");
                for (Fraktion regierungsfraktion : regierung
                     ) {
                    writer.write("\n - " + regierungsfraktion.getLabel());
                }
                writer.write("\n" + "-".repeat(100));

                // Oppositionsführer ist die Fraktion mit den meisten Abgeordneten, die nicht in der Regierung ist:
                Fraktion oppositionsFuehrer = oppositionsFraktionen.get(0);
                for (Fraktion f : oppositionsFraktionen) {
                    if (f.getMembers(wahlperiode).size() > oppositionsFuehrer.getMembers(wahlperiode).size()) {
                        oppositionsFuehrer = f;
                    }
                }
                writer.write("\nOppositionsführer: " + oppositionsFuehrer.getLabel());
            }
        }
        writer.close();
        // Ausgabe auf der Konsole
        output("Antworten/Abfrage 3d (Oppositionsfuehrer).txt");
    }


    /**
     * Abfrage 3e) - für Details siehe erzeugte .txt, Aufgabenstellung oder README
     */
    @Test
    public void abfrage3e() throws BiffException, IOException, ParserConfigurationException, ParseException, SAXException, BundestagException, InvalidFormatException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "src/main/resources/Bundestagsreden20",
                "src/main/resources/Abstimmungen");

        FileWriter file = new FileWriter("Antworten/Abfrage 3e.txt");
        BufferedWriter writer = new BufferedWriter(file);

        writer.write("\n\n" + "*".repeat(140));
        writer.write("\nABSTIMMUNGEN VON ABGEORDNETEN - GEORDNET NACH FRAKTION");
        writer.write("\n" + "*".repeat(140));
        writer.write("\n\n -- HINWEIS: Abgeordnete und Fraktionen ohne erfasste Abstimmungen werden nicht aufgeführt. -- \n");

        // Abstimmungen der Abgeordneten nach Fraktion geordnet ausgeben
        for (Fraktion fraktion : factory.listFraktionen()) {

            boolean ersterDerFraktion = true;
            Set<Abgeordneter> mitglieder = fraktion.getMembers();

            for (Abgeordneter abgeordneter : mitglieder) {

                // Nur Abgeordnete mit vorhandenen Stimmen aufführen
                if (!abgeordneter.listAbstimmungen().isEmpty()) {
                    if (ersterDerFraktion) {
                        writer.write("\n" + "-".repeat(140));
                        writer.write("\n" + fraktion.getLabel().toUpperCase());
                        writer.write("\n" + "-".repeat(140));
                        ersterDerFraktion = false;
                    }

                    writer.write("\n\n" + "*".repeat(140));
                    writer.write("\nID" + abgeordneter.getID() + " - " + abgeordneter.getVorname() + " " +
                            abgeordneter.getName() + " - TEILGENOMMENE ABSTIMMUNGEN");
                    writer.write("\n" + "*".repeat(140));

                    for (Abstimmung abstimmung : abgeordneter.listAbstimmungen()) {
                        writer.write("\n - " + abstimmung.getBeschreibung() + " [" + abstimmung.getErgebnis() + "]");
                    }
                }
            }
        }
        writer.close();
        // Ausgabe auf der Konsole
        output("Antworten/Abfrage 3e.txt");
    }

    /**
     * Abfrage 3f) - für Details siehe erzeugte .txt, Aufgabenstellung oder README
     */
    @Test
    public void abfrage3f() throws BiffException, IOException, ParserConfigurationException, ParseException, InvalidFormatException, SAXException, BundestagException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "src/main/resources/Bundestagsreden20",
                "");

        FileWriter file = new FileWriter("Antworten/Abfrage 3f.txt");
        BufferedWriter writer = new BufferedWriter(file);

        writer.write("\n\n" + "*".repeat(120));
        writer.write("\nFEHLTAGE ALLER ABGEORDNETEN - GEORDNET NACH FRAKTION - ABSTEIGEND SORTIERT");
        writer.write("\n" + "*".repeat(120));
        writer.write("\n\n -- HINWEIS: Abgeordnete mit den meisten Fehltagen jeder Fraktion sind jeweils ganz oben zu finden. -- ");
        writer.write("\n -- HINWEIS: Abgeordnete ohne erfasste Fehltage werden nicht aufgeführt. -- ");


        Set<Tuple<java.sql.Date, Set<Abgeordneter>>> abwesenheiten = factory.getAbwesenheiten();

        // Ergebnisse nach Fraktion bestimmen
        for (Fraktion fraktion : factory.listFraktionen()) {
            boolean ersterDerFraktion = true;
            List<Tuple<Abgeordneter, List<java.sql.Date>>> alleFehltageFraktion = new ArrayList<>();

            for (Abgeordneter abgeordneter : fraktion.getMembers()) {
                List<java.sql.Date> fehltage = new ArrayList<>();
                for (Tuple<java.sql.Date, Set<Abgeordneter>> sitzung : abwesenheiten) {
                    if (sitzung.getSecond().contains(abgeordneter)) {
                        fehltage.add(sitzung.getFirst());
                    }
                }
                if (!fehltage.isEmpty()) {
                    alleFehltageFraktion.add(new Tuple<>(abgeordneter, fehltage));
                }
            }

            // Ausgabe aller Abgeordneten der Fraktion - absteigend nach Häufigkeit der Fehltage
            // Fraktionen werden nur aufgeführt, falls Abgeordnete mit Fehltagen vorhanden
            // Abgeordnete ohne erfasste Fehltage werden ignoriert
            if (!alleFehltageFraktion.isEmpty()) {
                alleFehltageFraktion.sort(Comparator.comparing(o -> o.getSecond().size()));
                Collections.reverse(alleFehltageFraktion);

                for (Tuple<Abgeordneter, List<java.sql.Date>> tuple : alleFehltageFraktion) {
                    if (ersterDerFraktion) {
                        writer.write("\n\n" + "*".repeat(120));
                        writer.write("\n" + fraktion.getLabel().toUpperCase());
                        writer.write("\n" + "*".repeat(120));
                        ersterDerFraktion = false;
                    }
                    writer.write("\n" + "-".repeat(120));
                    writer.write("\nID" + tuple.getFirst().getID() + " - " + tuple.getFirst().getVorname()
                            + " " + tuple.getFirst().getName()
                            + " mit " + tuple.getSecond().size() + " Fehltagen: ");
                    List<Date> fehltageAbgeordneter = new ArrayList<>(tuple.getSecond());
                    Collections.sort(fehltageAbgeordneter);

                    int datesInLine = 0;
                    for (Date datum : fehltageAbgeordneter) {
                        if (datesInLine % 8 == 0) {
                            writer.write("\n             " + datum + "  ");
                        }
                        else {
                            writer.write(" " + datum + "  ");
                        }
                        datesInLine += 1;
                    }
                }
            }
        }
        writer.close();
        // Ausgabe auf der Konsole
        output("Antworten/Abfrage 3f.txt");
    }

    /**
     * Abfrage 3g) - für Details siehe erzeugte .txt, Aufgabenstellung oder README
     */
    @Test
    public void abfrage3g() throws BiffException, IOException, ParserConfigurationException, ParseException, InvalidFormatException, SAXException, BundestagException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "",
                "");

        FileWriter file = new FileWriter("Antworten/Abfrage 3g.txt");
        BufferedWriter writer = new BufferedWriter(file);

        writer.write("\n\n" + "*".repeat(120));
        writer.write("\nALLE ABGEORDNETEN - ABSTEIGEND SORTIERT NACH LÄNGSTER MITGLIEDSCHAFT IM BUNDESTAG");
        writer.write("\n" + "*".repeat(120));

        Map<Abgeordneter, List<Integer>> abgeordneterWP = new HashMap<>();

        for (Abgeordneter abgeordneter : factory.listAbgeordnete()) {

            // Alle Wahlperioden des Abgeordneten erfassen
            List<Integer> erfassteWahlperioden = new ArrayList<>();

            // Mehrfachmandate eines Abgeordneten ignorieren, sodass höchstens ein Mandat pro Wahlperiode existiert
            // → passiert automatisch über HashSet
            for (Mandat mandat : abgeordneter.listMandate()) {
                erfassteWahlperioden.add(mandat.getWahlperiode().getNumber());
            }
            abgeordneterWP.put(abgeordneter, erfassteWahlperioden);
        }

        // Sortierung der Abgeordneten nach Anzahl aktiver Wahlperioden
        int currBestDuration = 0;
        Abgeordneter currBest = null;

        List<Tuple<Abgeordneter, List<Integer>>> result = new ArrayList<>();

        while (!abgeordneterWP.isEmpty()) {
            for (Abgeordneter abgeordneter : abgeordneterWP.keySet()) {
                if (abgeordneterWP.get(abgeordneter).size() > currBestDuration) {
                    currBestDuration = abgeordneterWP.get(abgeordneter).size();
                    currBest = abgeordneter;
                }
            }
            List<Integer> wpSorted = new ArrayList<>(abgeordneterWP.get(currBest));
            Collections.sort(wpSorted);

            result.add(new Tuple<>(currBest, wpSorted));
            abgeordneterWP.remove(currBest);
            currBest = null;
            currBestDuration = 0;
        }

        // Ausgabe der Ergebnisse
        int counter = 1;

        for (Tuple<Abgeordneter, List<Integer>> tuple : result) {
            int digits = 0;
            int counterCopy = counter;

            while (counterCopy != 0) {
                counterCopy /= 10;
                ++ digits;
            }
            writer.write("\n" + "-".repeat(120));
            writer.write("\n#" + counter + " - ID" + tuple.getFirst().getID() + " - " + tuple.getFirst().getVorname() + " "
                    + tuple.getFirst().getName() + "\n" + " ".repeat(digits - 1) + "   - mit " + tuple.getSecond().size() + " aktiven Wahlperioden: " + tuple.getSecond());
            counter += 1;
        }
        writer.close();
        // Ausgabe auf der Konsole
        output("Antworten/Abfrage 3g.txt");
    }

    /**
     * Abfrage 3i) - für Details siehe erzeugte .txt, Aufgabenstellung oder README
     */
    @Test
    public void abfrage3hDirektwahl() throws BiffException, IOException, ParserConfigurationException, ParseException, InvalidFormatException, SAXException, BundestagException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "",
                "");

        FileWriter file = new FileWriter("Antworten/Abfrage 3h (Direktwahl).txt");
        BufferedWriter writer = new BufferedWriter(file);

        writer.write("\n\n" + "*".repeat(120));
        writer.write("\nALLE WAHLKREISE (DIREKTWAHLEN) MIT IHREN DOMINIERENDEN PARTEIEN - GETRENNT NACH WAHLPERIODE");
        writer.write("\n" + "*".repeat(120));
        writer.write("\n\n -- HINWEIS: Die Abfrage ergibt wenig Sinn, da in jedem Wahlkreis zu jeder Wahlperiode GENAU EIN DIREKTMANDAT existiert" +
                         "\n             und die Partei des entsprechenden Abgeordneten den Wahlkreis zu 100% dominiert.\n");

        List<Wahlperiode> wahlperiodenReversed = factory.getWahlperioden();
        Collections.reverse(wahlperiodenReversed);

        for (Wahlperiode wahlperiode : wahlperiodenReversed) {
            writer.write("\n" + "-".repeat(120));
            writer.write("\nWAHLPERIODE: " + wahlperiode.getNumber());
            writer.write("\n" + "-".repeat(120));
            List<Mandat> mandateSortiertNachWK = new ArrayList<>(wahlperiode.listMandate(Types.MANDAT.DIREKTWAHL));
            mandateSortiertNachWK.sort(Comparator.comparing(o -> {
                try {
                    return o.getWahlkreis().getNumber();
                } catch (BundestagException e) {
                    throw new RuntimeException(e);
                }
            }));
            for (Mandat mandat : mandateSortiertNachWK) {
                writer.write("\n Wahlkreis " + mandat.getWahlkreis().getNumber() +
                        " ".repeat(3 - (String.valueOf(mandat.getWahlkreis().getNumber()).length())) +
                        " --> zu 100% dominierende Partei: "+ mandat.getAbgeordneter().getPartei().getLabel());
            }
        }
        writer.close();
        // Ausgabe auf der Konsole
        output("Antworten/Abfrage 3h (Direktwahl).txt");
    }

    /**
     * Abfrage 3i) - für Details siehe erzeugte .txt, Aufgabenstellung oder README
     */
    @Test
    public void abfrage3hListenwahl() throws BiffException, IOException, ParserConfigurationException, ParseException, InvalidFormatException, SAXException, BundestagException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "",
                "");

        FileWriter file = new FileWriter("Antworten/Abfrage 3h (Listenwahl).txt");
        BufferedWriter writer = new BufferedWriter(file);

        writer.write("\n\n" + "*".repeat(120));
        writer.write("\nALLE LISTEN MIT IHREN DOMINIERENDEN PARTEIEN - GETRENNT NACH WAHLPERIODE");
        writer.write("\n" + "*".repeat(120) + "\n");
        writer.write("\n -- HINWEIS: Die dominierenden Parteien jeder Liste pro WP sind jeweils ganz oben aufgeführt. -- ");

        List<Wahlperiode> wahlperiodenReversed = factory.getWahlperioden();
        Collections.reverse(wahlperiodenReversed);

        // Für jede Wahlperiode ...
        for (Wahlperiode wahlperiode : wahlperiodenReversed) {
            writer.write("\n\n" + "*".repeat(120));
            writer.write("\nWAHLPERIODE: " + wahlperiode.getNumber());
            writer.write("\n" + "*".repeat(120));

            // ... die Mandate erfassen, die vom Typ LISTENWAHL sind
            List<Mandat> mandateLandesliste = new ArrayList<>(wahlperiode.listMandate(Types.MANDAT.LANDESLISTE));

            // Mandate nach Liste sortieren
            mandateLandesliste.sort(Comparator.comparing(o -> {
                try {
                    return getListe(o);
                } catch (AttributeNotFoundError e) {
                    throw new RuntimeException(e);
                }
            }));

            // Alle Listen der Wahlperiode erfassen und nach demselben Schema wie die Mandate sortieren
            Set<String> alleListen = new HashSet<>();

            for (Mandat mandat : mandateLandesliste) {
                alleListen.add(getListe(mandat));
            }

            List<String> alleListenSortiert = new ArrayList<>(alleListen);
            Collections.sort(alleListenSortiert);

            // Jedes Mandat der entsprechenden Liste zuordnen
            List<Tuple<String, Set<Mandat>>> listeAufMandate = new ArrayList<>();

            for (String liste : alleListenSortiert) {
                Set<Mandat> abgeordneteDerListe = new HashSet<>();
                for (Mandat mandat : mandateLandesliste) {
                    if (getListe(mandat).equals(liste)) {
                        abgeordneteDerListe.add(mandat);
                    }
                }
                listeAufMandate.add(new Tuple<>(liste, abgeordneteDerListe));
            }

            // Für jede Liste den Anteil der verschiedenen Parteien ermitteln und ausgeben
            for (Tuple<String, Set<Mandat>> tuple : listeAufMandate) {
                List<Tuple<Partei, Integer>> parteiAnzahl = new ArrayList<>();
                int summeMandate = 0;

                // Alle Parteien der Liste ermitteln
                Set<Partei> parteien = new HashSet<>();

                for (Mandat m : tuple.getSecond()) {
                    parteien.add(m.getAbgeordneter().getPartei());
                }

                // Mandate pro Partei zählen und zu parteiAnzahl hinzufügen
                for (Partei partei : parteien) {
                    int anzahlMandate = 0;
                    for (Mandat ma : tuple.getSecond()
                         ) {
                        if (ma.getAbgeordneter().getPartei().getLabel().equals(partei.getLabel())) {
                            anzahlMandate += 1;
                            summeMandate += 1;
                        }
                    }
                    parteiAnzahl.add(new Tuple<>(partei, anzahlMandate));
                }

                // Ausgabe der Ergebnisse für diese Liste
                writer.write("\n" + "-".repeat(120));
                writer.write("\nLISTE: " + tuple.getFirst());
                writer.write("\n" + "-".repeat(120));
                parteiAnzahl.sort(Comparator.comparing(Tuple::getSecond)); // Absteigend nach Prozentzahl sortieren
                Collections.reverse(parteiAnzahl);

                for (Tuple<Partei, Integer> t : parteiAnzahl) {
                    writer.write("\nMit "
                            + String.format("%.2f%%",(float) t.getSecond() * 100.0 / summeMandate)
                            + " : " + t.getFirst().getLabel());
                }
            }
        }
        writer.close();
        // Ausgabe auf der Konsole
        output("Antworten/Abfrage 3h (Listenwahl).txt");
    }

    /**
     * Abfrage 3i) - für Details siehe erzeugte .txt, Aufgabenstellung oder README
     */
    @Test
    public void abfrage3i() throws BiffException, IOException, ParserConfigurationException, ParseException, InvalidFormatException, SAXException, BundestagException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "",
                "");

        FileWriter file = new FileWriter("Antworten/Abfrage 3i.txt");
        BufferedWriter writer = new BufferedWriter(file);

        writer.write("\n\n" + "*".repeat(120));
        writer.write("\nALLE ERFASSTEN PRÄSIDENTEN, STELLVERTRETENDE PRÄSIDENTEN ODER SCHRIFTFÜHRER - GEORDNET NACH PARTEI");
        writer.write("\n" + "*".repeat(120));
        writer.write("\n\n -- HINWEIS: Parteien ohne Abgeordneten mit solchen Positionen werden nicht aufgeführt. -- \n");

        // Alle vorhandenen Parteien erfassen:
        Set<Partei> parteien = new HashSet<>();

        for (Abgeordneter mdb : factory.listAbgeordnete()) {
            parteien.add(mdb.getPartei());
        }

        // Für die schöne Ausgabe
        int maxLengthName = 0;
        for (Abgeordneter mdb : factory.listAbgeordnete()) {
            if ((mdb.getVorname() + " " + mdb.getName()).length() > maxLengthName) {
                maxLengthName = (mdb.getVorname() + " " + mdb.getName()).length();
            }
        }

        // Innerhalb jeder Partei nach Abgeordneten mit den gefragten Positionen suchen
        for (Partei partei : parteien) {
            boolean ersterDerPartei = true;
            for (Abgeordneter abgeordneter : partei.getMembers()) {
                Set<Mitgliedschaft> result = new HashSet<>();

                for (Mitgliedschaft mitgliedschaft : abgeordneter.listMitgliedschaften()) {
                    if (mitgliedschaft.getGruppe().getLabel().equals("Präsidium")
                     || mitgliedschaft.getGruppe().getLabel().equals("Schriftführer")) {

                        result.add(mitgliedschaft);
                    }
                }

                // Ausgabe der Ergebnisse (Parteien ohne PräsidentInnen und SchriftführerInnen werden nicht aufgeführt)
                if (!result.isEmpty()) {
                    if (ersterDerPartei) {
                        writer.write("\n" + "-".repeat(120));
                        writer.write("\n" + partei.getLabel().toUpperCase());
                        writer.write("\n" + "-".repeat(120));
                        ersterDerPartei = false;
                    }
                    writer.write("\nID" + abgeordneter.getID() + " - " + abgeordneter.getVorname() + " " + abgeordneter.getName()
                            + " ".repeat(maxLengthName - (abgeordneter.getVorname() + " " + abgeordneter.getName()).length()));
                    for (Mitgliedschaft m : result) {
                        // Obfrau/Obmann-Schriftführer:
                        if (!m.getFunktion().equals("k. A.") && (m.getFunktion().equals("Obfrau") || m.getFunktion().equals("Obmann"))) {
                            writer.write(" - WP" + m.getWahlperiode().getNumber() + ": " + m.getFunktion() + " (" + m.getGruppe().getLabel() + ")");
                        }
                        // (Vize-)PräsidentIn
                        else if (!m.getFunktion().equals("k. A.")) {
                            writer.write(" - WP" + m.getWahlperiode().getNumber() + ": " + m.getFunktion());
                        }
                        // Schriftführer:
                        else {
                            writer.write(" - WP" + m.getWahlperiode().getNumber() + ": " + m.getGruppe().getLabel());
                            break;
                        }
                    }
                }
            }
        }
        writer.close();
        // Ausgabe auf der Konsole
        output("Antworten/Abfrage 3i.txt");
    }
}
