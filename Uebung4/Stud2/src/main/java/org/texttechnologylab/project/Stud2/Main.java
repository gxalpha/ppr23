package org.texttechnologylab.project.Stud2;

import org.texttechnologylab.project.Stud2.data.BundestagFactory;
import org.texttechnologylab.project.Stud2.data.impl.BundestagFactoryImpl;
import org.texttechnologylab.project.Stud2.database.MongoDBConnectionHandler;
import org.texttechnologylab.project.Stud2.database.impl.MongoDBConnectionHandlerImpl;
import org.texttechnologylab.project.Stud2.helper.NLPHelper;
import org.texttechnologylab.project.Stud2.helper.RESTHelper;
import org.texttechnologylab.project.Stud2.helper.StringHelper;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Die Main-Funktion
 *
 * @author Stud2
 */
public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("\n" + StringHelper.getCurrDateTimeFormatted() + " [BEFORE WE START] Do you want to drop the entire database and parse every XML-File again? Please answer below and press [ENTER]. (y/n)");

        // Falls parseDataAgain = true: Einlesen der Plenarprotokolle und Upload in der MongoDB OHNE Analyse der Reden!
        // → Die alten CAS-Objekte gehen also alle verloren!
        boolean parseDataAgain;

        // Der Nutzer darf entscheiden, ob die Dateien alle neu eingelesen werden sollen:
        Scanner in = new Scanner(System.in);

        while (true) {
            String answer = in.nextLine();
            if (answer.equalsIgnoreCase("n")) {
                System.out.println("\n" + StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Proceeding without parsing the XML-Files again.\n");
                parseDataAgain = false;
                break;

            } else if (answer.equalsIgnoreCase("y")) {
                System.out.println("\n[WARNING] All NLP data currently saved in MongoDB will be deleted. Please enter the password below - you only have one try!");
                String password = in.nextLine();
                if (password.equals("123456789")) {
                    parseDataAgain = true;
                    System.out.println("\n" + StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Proceeding with parsing the XML-Files again.\n");
                } else {
                    System.out.println("\n" + StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Invalid password. Proceeding without parsing every XML-File again. No NLP data will be deleted.\n");
                    parseDataAgain = false;
                }
                break;
            } else {
                System.out.println("\n" + StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Invalid answer. Please enter 'y' [Yes] or 'n' [No].");
            }
        }

        MongoDBConnectionHandler mongoDB = new MongoDBConnectionHandlerImpl();

        BundestagFactory bundestagFactory = new BundestagFactoryImpl(
                "src/main/resources/MdB-Stammdaten/MDB_STAMMDATEN.XML",
                "src/main/resources/Bundestagsreden20",
                parseDataAgain, mongoDB);

        NLPHelper nlpHelper = new NLPHelper(mongoDB);

        RESTHelper restHelper = new RESTHelper(bundestagFactory, mongoDB, nlpHelper);
        TimeUnit.SECONDS.sleep(2);
        System.out.println("\n" + StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Please open the following URL in your browser (e.g. Safari) to start the Web-Application: http://localhost:1234/InsightBundestag/startseite");
    }
}
