package org.texttechnologylab.project.Stud2.impl.helper;

import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Abstimmung;
import org.texttechnologylab.project.data.BundestagObject;
import org.texttechnologylab.project.data.Mandat;
import org.texttechnologylab.project.data.Mitgliedschaft;
import org.texttechnologylab.project.data.Rede;
import org.texttechnologylab.project.exception.BundestagException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Eine Klasse mit Funktionen, die bestimmte Bundestagsobjekte schön auf der Konsole ausgibt
 * IRRELEVANT für die Lösung der Aufgabe, war nur zum Testen da und es wäre viel zu schade, alles zu löschen.
 *
 * @author Stud2
 */
public class ShowFunctions {
    /**
     * Gibt eine Rede auf der Konsole aus.
     *
     * @param rede auf der Konsole auszugebende Rede
     */
    public static void showRede(Rede rede) throws BundestagException {
        System.out.println("ID der Rede: " + rede.getID());
        System.out.println("DATUM: " + rede.getDate());
        System.out.println("ABGEORDNETER: " + rede.getRedner().getName() + ", " + rede.getRedner().getVorname() + " (" + rede.getRedner().getPartei().getLabel() + ")");
        System.out.println("\nTEXT:");
        String[] redeFormatted = rede.getText().split("(?<=\\G.{100})");
        for (String line : redeFormatted) {
            System.out.println(line);
        }
    }

    /**
     * Gibt alle übergebenen Abgeordneten und deren wichtigsten Attribute auf der Konsole aus
     *
     * @param abgeordnete alle auf der Konsole anzuzeigenden Abgeordneten
     */
    public static void showAbgeordnete(Set<Abgeordneter> abgeordnete) throws BundestagException {
        List<Abgeordneter> sortedAbgeordnete = new ArrayList<>(abgeordnete);
        sortedAbgeordnete.sort(Comparator.comparing(BundestagObject::getLabel)); // Abgeordnete nach ID sortieren

        for (Abgeordneter mdb : sortedAbgeordnete
        ) {
            System.out.println("\n**************************************************************************" +
                    "*************************************************************************");
            System.out.println("ABGEORDNETER (ID" + mdb.getID() + "): " + mdb.getName() + ", " + mdb.getVorname() + " ("
                    + mdb.getPartei().getLabel() + ")");
            System.out.println("****************************************************************" +
                    "***********************************************************************************");
            System.out.println("                                                                     MANDATE");

            List<Mandat> mandate = new ArrayList<>(mdb.listMandate());
            mandate.sort(Comparator.comparing(o -> o.getWahlperiode().getLabel())); // Mandate sortieren nach WP

            for (Mandat mandat : mandate) {
                System.out.println("----------------------------------------------------------------" +
                        "-----------------------------------------------------------------------------------");
                System.out.println(mandat.getTyp() + " (WP" + mandat.getWahlperiode().getNumber() + ")");
                if (!mandat.getWahlkreis().getLabel().equals("k. A.")) {
                    System.out.println("Wahlkreis: " + mandat.getWahlkreis().getNumber());
                }

                System.out.println("\nMITGLIEDSCHAFTEN während dieses Mandats: ");
                for (Mitgliedschaft m : mandat.listMitgliedschaft()) {
                    if (m.getFunktion().equals("k. A.")) {
                        System.out.println(" - " + m.getGruppe().getLabel());
                    }
                    else {
                        System.out.println(" - " + m.getGruppe().getLabel() + " [" + m.getFunktion() + "]");
                    }
                }
            }
            System.out.println("----------------------------------------------------------------" +
                    "-----------------------------------------------------------------------------------");
            System.out.println("                                                                 ABSTIMMUNGEN");
            for (Abstimmung stimme : mdb.listAbstimmungen()) {
                System.out.println("----------------------------------------------------------------" +
                        "-----------------------------------------------------------------------------------");
                System.out.println("THEMA:  " + stimme.getBeschreibung());
                System.out.println("Datum:  " + stimme.getLabel().substring(0, 10));
                System.out.println("STIMME: " + stimme.getErgebnis());
            }
        }
    }
}
