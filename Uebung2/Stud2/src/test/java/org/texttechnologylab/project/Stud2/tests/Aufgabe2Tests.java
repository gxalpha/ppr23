package org.texttechnologylab.project.Stud2.tests;

import jxl.read.biff.BiffException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.exception.BundestagException;
import org.texttechnologylab.project.Stud2.impl.data.WahlperiodeImpl;
import org.texttechnologylab.project.Stud2.impl.exception.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud2.impl.helper.BundestagFactoryImpl;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testet eine Auswahl an Funktionen des Programms auf deren Funktionalität und Verhalten bei Fehleingaben
 *
 * @author Stud2
 */
public class Aufgabe2Tests {
    /**
     * Testet, dass der Nutzer falsche/korrekte Pfade in den Konstruktor der Factory angibt
     */
    @Test
    public void testFactory() throws BiffException, IOException, ParserConfigurationException, ParseException, InvalidFormatException, SAXException, BundestagException {
        // Negativtests.
        // Der Pfad zu den Stammdaten des Abgeordneten muss korrekt sein:
        assertThrows(org.xml.sax.SAXParseException.class, () -> new BundestagFactoryImpl(
                "",
                "",
                ""));

        assertThrows(FileNotFoundException.class, () -> new BundestagFactoryImpl(
                "Hallo, Givara, wie geht es dir",
                "",
                ""));

        // Falls nur der Ordner, in der MDB_STAMMDATEN ist, übergeben wird, aber nicht die .XML selbst
        assertThrows(org.xml.sax.SAXParseException.class, () -> new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten",
                "",
                ""));

        // Falls die anderen Pfade falsch sind
        assertThrows(AssertionError.class, () -> new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "Hallo Givara",
                "Hallo Givara!"));

        // Positivtests.
        assertDoesNotThrow(() -> new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "",
                ""));

        assertDoesNotThrow(() -> new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "src/main/resources/Bundestagsreden20",
                ""));
    }

    /**
     * Testet parseAbgeordneter()
     */
    @Test
    public void testParseAbgeordneter() throws BiffException, IOException, ParserConfigurationException, ParseException, InvalidFormatException, SAXException, BundestagException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "",
                "");

        // Es sollte 4376 Abgeordnete geben
        assertEquals(4376, factory.listAbgeordnete().size());
    }

    /**
     * Testet parseReden()
     */
    @Test
    public void testParseReden() throws BiffException, IOException, ParserConfigurationException, ParseException, InvalidFormatException, SAXException, BundestagException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "src/main/resources/Bundestagsreden20",
                "");

        // Angenommen es wurden auch die gegebenen 131 Bundestagsreden in den entsprechenden Ordner eingefügt
        assertEquals(15478, factory.listReden().size());
    }

    /**
     * Testet getAbgeordneterByID()
     */
    @Test
    public void testGetAbgeordneterByID() throws BundestagException, BiffException, IOException, ParserConfigurationException, ParseException, InvalidFormatException, SAXException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "",
                "");
        // Falls ID nicht existiert oder existiert und negativ:
        assertThrows(AbgeordneterNotFoundException.class, () -> factory.getAbgeordneterByID(1234));
        assertThrows(AbgeordneterNotFoundException.class, () -> factory.getAbgeordneterByID(999900101));
        assertThrows(AbgeordneterNotFoundException.class, () -> factory.getAbgeordneterByID(-1100197));

        // Positivtests
        Abgeordneter abgeordneter1 = factory.getAbgeordneterByID(11001947);
        Abgeordneter abgeordneter2 = factory.getAbgeordneterByID(11001988);
        assertEquals("Abgeordneter: 11001947", abgeordneter1.getLabel());
        assertEquals("Abgeordneter: 11001988", abgeordneter2.getLabel());
    }

    /**
     * Testet getAbgeordneterByName()
     */
    @Test
    public void testGetAbgeordneterByName() throws BundestagException, BiffException, IOException, ParserConfigurationException, ParseException, InvalidFormatException, SAXException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "",
                "");

        // Falls Abgeordneter nicht existiert
        assertThrows(AbgeordneterNotFoundException.class, () -> factory.getAbgeordneterByName("A", "B"));

        // Positivtests
        Abgeordneter abgeordneter1 = factory.getAbgeordneterByName("Scholz", "Olaf");
        Abgeordneter abgeordneter2 = factory.getAbgeordneterByName("Bas", "Bärbel"); // Beispiel für Umlaute
        assertEquals("Scholz", abgeordneter1.getName());
        assertEquals("Olaf", abgeordneter1.getVorname());
        assertEquals("Bas", abgeordneter2.getName());
        assertEquals("Bärbel", abgeordneter2.getVorname());
    }

    /**
     * Testet getAbgeordneterByWahlperiodeAndName()
     */
    @Test
    public void testGetAbgeordneterByWahlperiodeAndName() throws BundestagException, BiffException, IOException, ParserConfigurationException, ParseException, InvalidFormatException, SAXException {
        BundestagFactoryImpl factory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "src/main/resources/Bundestagsreden20",
                "");

        // Falls Abgeordneter nicht existiert
        assertThrows(AbgeordneterNotFoundException.class, () -> factory.getAbgeordneterByWahlperiodeAndName(100, "A", "B"));

        // Positivtests
        Abgeordneter abgeordneter1 = factory.getAbgeordneterByWahlperiodeAndName(20, "Scholz", "Olaf");
        Abgeordneter abgeordneter2 = factory.getAbgeordneterByWahlperiodeAndName(20,"Bas", "Bärbel"); // Beispiel für Umlaute
        assertEquals("Scholz", abgeordneter1.getName());
        assertEquals("Olaf", abgeordneter1.getVorname());
        assertEquals("Bas", abgeordneter2.getName());
        assertEquals("Bärbel", abgeordneter2.getVorname());
    }
}
