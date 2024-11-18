package org.texttechnologylab.project.Stud1.Uebung3;

import org.texttechnologylab.project.Stud1.data.BundestagFactory;
import org.texttechnologylab.project.Stud1.exceptions.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud1.exceptions.BadDataFormatException;

import java.io.File;

public class TestHelper {

    /**
     * Private Hilfsfunktion zum Einlesen von Abgeordneten in den Tests
     *
     * @param factory Factory, die einlesen soll
     * @throws BadDataFormatException Wenn das Einlesen scheitert
     */
    static void readStammdaten(BundestagFactory factory) throws BadDataFormatException {
        System.out.println("Lese Abgeordnete ein...");
        File stammdaten = new File("src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML");
        if (!stammdaten.exists() || stammdaten.isDirectory()) {
            throw new RuntimeException("'src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML'-Datei nicht gefunden.");
        }

        factory.readAbgeordnete(stammdaten);
        System.out.println("Abgeordnete erfolgreich eingelesen.");
    }

    /**
     * Private Hilfsfunktion zum Einlesen von Protokollen in den Tests
     *
     * @param factory Factory, die einlesen soll
     * @throws AbgeordneterNotFoundException Wenn ein Abgeordneter gefunden wird, der nicht eingelesen wurde
     * @throws BadDataFormatException        Wenn das Einlesen scheitert
     */
    static void readProtokolle(BundestagFactory factory) throws AbgeordneterNotFoundException, BadDataFormatException {
        // Disappointingly, there doesn't appear to be a way to use the class loader to get a directory to iterate over
        File directory = new File("src/main/resources/Bundestagsreden20");
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("'Resources/Bundestagsreden20'-Ordner nicht gefunden.");
        }

        File[] files = directory.listFiles((dir, name) -> name.matches("[0-9]+\\.xml"));
        System.out.println("Analysiere " + files.length + " Protokolle...");
        for (File file : files) {
            System.out.println("    Analysiere Protokoll " + file.getName() + "...");
            factory.readProtokoll(file);
        }
        System.out.println("Protokollanalyse abgeschlossen.");
    }
}
