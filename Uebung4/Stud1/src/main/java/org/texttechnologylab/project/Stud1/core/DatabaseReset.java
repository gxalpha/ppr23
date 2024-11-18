package org.texttechnologylab.project.Stud1.core;

import org.texttechnologylab.project.Stud1.data.BundestagFactory;
import org.texttechnologylab.project.Stud1.database.MongoDBConnectionHandler;
import org.texttechnologylab.project.Stud1.exceptions.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud1.exceptions.BadDataFormatException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import static org.texttechnologylab.project.Stud1.core.TestHelper.readProtokolle;
import static org.texttechnologylab.project.Stud1.core.TestHelper.readStammdaten;

/**
 * Ausführbase Klasse zum Resetten der Datenbank
 */
public class DatabaseReset {
    /**
     * Ausführbare Methode zum Zurücksetzen der Stammdaten und Redeinhalte der Datenbank.
     * Gefährlich, use at your own risk (or better: Don't use at all, unless you *really* screwed up. In which case, also delete the other collections and just start from scratch).
     *
     * @param args Command-Line-Arguments (ignoriert).
     */
    public static void main(String[] args) throws BadDataFormatException, AbgeordneterNotFoundException, InterruptedException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Möchtest du wirklich alle Basisdaten zurücksetzen? Dies löscht auch ausgelesene NLP-Daten (die CAS-Repräsentation bleibt erhalten, die Daten müssen aber manuell wieder zu den Reden hinzugefügt werden).\nTippe \"ja\":");
        if (!scanner.nextLine().equals("ja")) {
            System.out.println("Nicht \"ja\" getippt. Programm wird beendet.");
            System.exit(0);
        }

        MongoDBConnectionHandler handler;
        try {
            handler = new MongoDBConnectionHandler(new FileInputStream("config.ini"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BundestagFactory factory = BundestagFactory.newInstance();
        readStammdaten(factory);
        readProtokolle(factory);
        handler.resetBaseData(factory);

        System.out.println("Erfolg. Zum Löschen der CAS-Daten oder der Logs, droppe die entsprechenden Collections manuell.");
    }
}
