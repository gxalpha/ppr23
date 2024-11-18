package org.texttechnologylab.project.Stud1.Uebung2.tests;

import org.texttechnologylab.project.Stud1.Uebung2.data.BundestagFactory;
import org.texttechnologylab.project.Stud1.Uebung2.exceptions.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud1.Uebung2.exceptions.BadDataFormatException;
import org.texttechnologylab.project.Stud1.Uebung2.exceptions.WahlperiodeNotFoundException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestHelper {

    /**
     * Private Hilfsfunktion zum Einlesen von Abgeordneten in den Tests
     *
     * @param factory Factory, die einlesen soll
     * @throws BadDataFormatException Wenn das Einlesen scheitert
     */
    static void readStammdaten(BundestagFactory factory) throws BadDataFormatException {
        System.out.println("Lese Abgeordnete ein...");
        File stammdaten = new File("Resources/MdB-Stammdaten/MDB_STAMMDATEN.XML");
        if (!stammdaten.exists() || stammdaten.isDirectory()) {
            throw new RuntimeException("'Resources/MdB-Stammdaten/MDB_STAMMDATEN.XML'-Datei nicht gefunden.");
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
        File directory = new File("Resources/Bundestagsreden20");
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

    /**
     * Private Hilfsfunktion zum parallelen(!) Einlesen von Abstimmungen in den Tests.
     * Wenn eine Datei nicht eingelesen werden kann, wird sie einfach übersprungen.
     *
     * @throws AbgeordneterNotFoundException Wenn ein Abgeordneter eingelesen wird, der nicht bekannt ist
     * @throws WahlperiodeNotFoundException  Wenn eine Abstimmung in einer unbekannten Wahlperiode stattfindet
     */
    static void readAbstimmungen(BundestagFactory factory) throws AbgeordneterNotFoundException, WahlperiodeNotFoundException {
        File directory = new File("Resources/Abstimmungen");
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("'Resources/Abstimmungen'-Ordner nicht gefunden.");
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".xlsx") || name.endsWith(".xls"));
        System.out.println("Analysiere " + files.length + " Abstimmungen...");

        List<Thread> threads = new ArrayList<>();
        List<Throwable> throwables = new ArrayList<>();

        for (File file : files) {
            Thread thread = new Thread(() -> {
                System.out.println("    Analysiere Abstimmung " + file.getName() + "...");
                try {
                    if (file.getName().endsWith(".xls")) {
                        factory.readAbstimmungXls(file);
                    } else if (file.getName().endsWith(".xlsx")) {
                        factory.readAbstimmungXlsx(file);
                    }
                } catch (BadDataFormatException e) {
                    System.err.println("    Fehler beim Analysieren von " + file.getName() + ": " + e.getMessage());
                } catch (AbgeordneterNotFoundException | WahlperiodeNotFoundException e) {
                    Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                }

            });
            threads.add(thread);
            thread.setName(file.getName());
            thread.setUncaughtExceptionHandler((t, e) -> {
                throwables.add(e);
            });
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        for (Throwable throwable : throwables) {
            if (throwable instanceof AbgeordneterNotFoundException) {
                throw (AbgeordneterNotFoundException) throwable;
            } else if (throwable instanceof WahlperiodeNotFoundException) {
                throw (WahlperiodeNotFoundException) throwable;
            } else {
                throw new RuntimeException(throwable);
            }
        }
        System.out.println("Protokollanalyse abgeschlossen.");
    }

}
