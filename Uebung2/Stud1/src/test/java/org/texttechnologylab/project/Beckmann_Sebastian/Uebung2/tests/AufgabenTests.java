package org.texttechnologylab.project.Stud1.Uebung2.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.texttechnologylab.project.Stud1.Uebung2.data.BundestagFactory;
import org.texttechnologylab.project.Stud1.Uebung2.data.Fehltag;
import org.texttechnologylab.project.Stud1.Uebung2.data.Landesliste;
import org.texttechnologylab.project.data.*;
import org.texttechnologylab.project.exception.BundestagException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.texttechnologylab.project.Stud1.Uebung2.tests.TestHelper.*;

/**
 * Test-Klasse zum Beantworten der Fragen in Aufgabe 3
 */
@DisplayName("Aufgabe 3")
public class AufgabenTests {
    /**
     * Private Hilfsfunktion zum Erstellen des Dateien-Schreibers und schönen Formatieren der Konsole
     *
     * @param file Ergebnis-Datei
     * @return AufgabenWriter, in die Ergebnisse geschrieben werden sollen
     */
    private AufgabenWriter beginneErgebnisse(File file) {
        System.out.println();
        System.out.println("================== BEGIN ERGEBNISSE ==================");
        System.out.println();
        return new AufgabenWriter(file);
    }


    /**
     * @param alleReden   Zu berücksichtigende Reden
     * @param abgeordnete Abgeordnete zum Auflisten
     */
    void aufgabe3a_printRedenzahlNachHauefigkeit(Set<Rede> alleReden, Set<Abgeordneter> abgeordnete, AufgabenWriter output) {
        Map<Abgeordneter, Set<Rede>> abgeordneterRedeMap = new HashMap<>();
        for (Rede rede : alleReden) {
            if (!abgeordnete.contains(rede.getRedner())) {
                continue;
            }

            if (!abgeordneterRedeMap.containsKey(rede.getRedner())) {
                abgeordneterRedeMap.put(rede.getRedner(), new HashSet<>());
            }
            abgeordneterRedeMap.get(rede.getRedner()).add(rede);
        }


        abgeordnete.stream().sorted((first, second) -> {
            int firstReden = 0;
            if (abgeordneterRedeMap.containsKey(first)) {
                firstReden = abgeordneterRedeMap.get(first).size();
            }
            int secondReden = 0;
            if (abgeordneterRedeMap.containsKey(second)) {
                secondReden = abgeordneterRedeMap.get(second).size();
            }
            // Primär nach Redenanzahl sortieren
            if (secondReden != firstReden) {
                return secondReden - firstReden;
            }

            // Bei 0 Reden brauchen wir auch keine Länge rechnen (beide sind 0, weil gleich.)
            if (firstReden == 0) {
                return 0;
            }

            // Atomic weil IntelliJ sich sonst beschwert.
            // Kann sein dass der das multithreaded.
            AtomicInteger firstLaenge = new AtomicInteger();
            abgeordneterRedeMap.get(first).forEach(rede -> firstLaenge.addAndGet(rede.getText().length()));
            AtomicInteger secondLaenge = new AtomicInteger();
            abgeordneterRedeMap.get(second).forEach(rede -> secondLaenge.addAndGet(rede.getText().length()));
            return secondLaenge.get() - firstLaenge.get();
        }).forEach(abgeordneter -> {
            int reden = 0;
            if (abgeordneterRedeMap.containsKey(abgeordneter)) {
                reden = abgeordneterRedeMap.get(abgeordneter).size();
            }
            AtomicInteger laenge = new AtomicInteger();
            if (abgeordneterRedeMap.containsKey(abgeordneter)) {
                abgeordneterRedeMap.get(abgeordneter).forEach(rede -> laenge.addAndGet(rede.getText().length()));
            }

            output.println("    " + abgeordneter.getLabel() + " (" + reden + " Reden, gesamt " + laenge.get() + " Zeichen)");
        });
    }

    /**
     * Implementierung von Aufgabe 3a)
     */
    @Test
    @DisplayName("a)")
    void aufgabe3a() {
        BundestagFactory factory = BundestagFactory.newInstance();
        assertDoesNotThrow(() -> readStammdaten(factory));
        assertDoesNotThrow(() -> readProtokolle(factory));

        AufgabenWriter output = beginneErgebnisse(new File("Antworten/Uebung2_Antwort_a.txt"));
        output.println("Hinweise zur Aufgabe: Sortiert primär nach Redenanzahl, sekundär nach Redenlänge\n\n");

        Set<Abgeordneter> alleBekanntenAbgeordneten = new HashSet<>();

        output.println("TEIL 1: Nach Fraktionen:");
        factory.listFraktionen().forEach(fraktion -> {
            output.println(fraktion.getLabel());
            aufgabe3a_printRedenzahlNachHauefigkeit(factory.listReden(), fraktion.getMembers(), output);
        });
        output.println();

        output.println("TEIL 2: Nach Parteien:");
        factory.listParteien().forEach(partei -> {
            output.println(partei.getLabel());
            aufgabe3a_printRedenzahlNachHauefigkeit(factory.listReden(), partei.getMembers(), output);
            if (!partei.getLabel().equals("Plos")) {
                alleBekanntenAbgeordneten.addAll(partei.getMembers());
            }
        });
        output.println();

        /* Es gibt keine Abgeordneten, die weder in Partei noch Fraktion waren.
         * Daher sind hier nur die parteilosen aufgelistet. */
        output.println("TEIL 3: Ohne Parteien:");
        Set<Abgeordneter> lonelyAbgeordnete = factory.listAbgeordnete();
        lonelyAbgeordnete.removeAll(alleBekanntenAbgeordneten);
        aufgabe3a_printRedenzahlNachHauefigkeit(factory.listReden(), lonelyAbgeordnete, output);
    }

    /**
     * Implementierung von Aufgabe 3b)
     */
    @Test
    @DisplayName("b)")
    void aufgabe3b() {
        BundestagFactory factory = BundestagFactory.newInstance();
        assertDoesNotThrow(() -> readStammdaten(factory));
        AufgabenWriter output = beginneErgebnisse(new File("Antworten/Uebung2_Antwort_b.txt"));

        Set<Ausschuss> ausschuesse = factory.listAusschuesse();
        ausschuesse.forEach(ausschuss -> {
            Map<Fraktion, Set<Mitgliedschaft>> fraktionenMap = new HashMap<>();
            // I swear this works
            ausschuss.getMembers().forEach(member -> member.listMitgliedschaften().forEach(mitgliedschaft -> {
                Gruppe gruppe = mitgliedschaft.getGruppe();
                if (gruppe instanceof Fraktion) {
                    Fraktion fraktion = (Fraktion) gruppe;
                    Set<Mitgliedschaft> mitgliedschaften;
                    if (fraktionenMap.containsKey(fraktion)) {
                        mitgliedschaften = fraktionenMap.get(fraktion);
                    } else {
                        mitgliedschaften = new HashSet<>();
                        fraktionenMap.put(fraktion, mitgliedschaften);
                    }
                    mitgliedschaften.add(mitgliedschaft);
                }
            }));
            List<Fraktion> fraktionenList = fraktionenMap.keySet().stream().sorted((first, second) -> fraktionenMap.get(second).size() - fraktionenMap.get(first).size()).collect(Collectors.toList());

            output.println(ausschuss.getLabel());
            fraktionenList.forEach(fraktion -> {
                Set<Mitgliedschaft> mitgliedschaften = fraktionenMap.get(fraktion);
                output.println("    " + fraktion.getLabel() + ", " + mitgliedschaften.size() + " Mitgliedschaften:");
                mitgliedschaften.forEach(mitgliedschaft -> output.println("        " + mitgliedschaft.getAbgeordneter().getLabel() + " (" + mitgliedschaft.getWahlperiode().getLabel() + ")"));
            });
            output.println();
        });
    }

    /**
     * Private Hilfsfunktion zum Ausgeben aller Ausschusse, in denen ein Abgeordneter ist
     *
     * @param abgeordneter Der Abgeordnete
     * @param wahlperiode  Wahlperiode der Ausschusse. null für alle Wahlperioden
     * @param output       AufgabenWriter, in den geschrieben werden muss
     */
    private void aufgabe3c_printAbgeordnetenAusschuesse(Abgeordneter abgeordneter, Wahlperiode wahlperiode, AufgabenWriter output) {
        Map<Ausschuss, Integer> ausschussMap = new HashMap<>();
        abgeordneter.listMitgliedschaften().forEach(mitgliedschaft -> {
            if (wahlperiode != null && !mitgliedschaft.getWahlperiode().equals(wahlperiode)) {
                return; // Wie continue.
            }
            Gruppe gruppe = mitgliedschaft.getGruppe();
            if (gruppe instanceof Ausschuss) {
                Ausschuss ausschuss = (Ausschuss) gruppe;
                int count = 0;
                if (ausschussMap.containsKey(ausschuss)) {
                    count = ausschussMap.get(ausschuss);
                }
                count++;
                ausschussMap.put(ausschuss, count);
            }
        });

        output.println("    " + abgeordneter.getLabel());
        if (ausschussMap.isEmpty()) {
            if (wahlperiode == null) {
                output.println("        War noch nie in einem Ausschuss");
            } else {
                output.println("        War in " + wahlperiode.getLabel() + " in keinem Ausschuss");
            }
        } else {
            ausschussMap.forEach((ausschuss, count) -> output.println("        " + ausschuss.getLabel() + " (" + count + " Mal)"));
        }
    }

    /**
     * Implementierung von Aufgabe 3c)
     */
    @Test
    @DisplayName("c)")
    void aufgabe3c() {
        BundestagFactory factory = BundestagFactory.newInstance();
        assertDoesNotThrow(() -> readStammdaten(factory));

        AufgabenWriter output = beginneErgebnisse(new File("Antworten/Uebung2_Antwort_c.txt"));

        /* Teil 1: Gruppiert nach Fraktionen */
        output.println("NACH FRAKTION:");
        Map<Fraktion, Set<Abgeordneter>> fraktionenMap = new HashMap<>();
        factory.listAbgeordnete().forEach(abgeordneter -> {
            abgeordneter.listMitgliedschaften().forEach(mitgliedschaft -> {
                Gruppe gruppe = mitgliedschaft.getGruppe();
                if (gruppe instanceof Fraktion) {
                    Set<Abgeordneter> abgeordnete;
                    if (fraktionenMap.containsKey((Fraktion) gruppe)) {
                        abgeordnete = fraktionenMap.get((Fraktion) gruppe);
                    } else {
                        abgeordnete = new HashSet<>();
                        fraktionenMap.put((Fraktion) gruppe, abgeordnete);
                    }
                    abgeordnete.add(abgeordneter);
                }
            });
        });
        fraktionenMap.forEach((fraktion, abgeordnete) -> {
            output.println(fraktion.getLabel());
            abgeordnete.forEach(abgeordneter -> aufgabe3c_printAbgeordnetenAusschuesse(abgeordneter, null, output));
            output.println();
        });
        output.println();

        /* Teil 2: Gruppiert nach Wahlperioden */
        output.println("\nNACH WAHLPERIODEN:");
        factory.listWahlperioden().stream().sorted(Comparator.comparingInt(Wahlperiode::getNumber)).forEach(wahlperiode -> {
            output.println(wahlperiode.getLabel());
            factory.listAbgeordnete().forEach(abgeordneter -> aufgabe3c_printAbgeordnetenAusschuesse(abgeordneter, wahlperiode, output));
            output.println();
        });
    }

    /**
     * Implementierung von Aufgabe 3d)
     */
    @Test
    @DisplayName("d)")
    void aufgabe3d() {
        BundestagFactory factory = BundestagFactory.newInstance();
        assertDoesNotThrow(() -> readStammdaten(factory));
        AufgabenWriter output = beginneErgebnisse(new File("Antworten/Uebung2_Antwort_d.txt"));

        output.println("TEIL 1: Am häufigsten Vorsitzende*r bzw stellvertretende*r Vorsitzende*r in einem Ausschuss:");
        Map<Abgeordneter, Integer> abgeordnetenMap = new HashMap<>();
        factory.listMitgliedschaften().forEach(mitgliedschaft -> {
            if (!(mitgliedschaft.getGruppe() instanceof Ausschuss)) {
                return;
            }

            try {
                if (!mitgliedschaft.getFunktion().toLowerCase().contains("vorsitzende" /* Alle Geschlechter, sowie Vorsitz und Stellvertretung */)) {
                    return;
                }
            } catch (BundestagException ignored) {
                return;
            }

            Abgeordneter abgeordneter = mitgliedschaft.getAbgeordneter();
            int anzahl = abgeordnetenMap.getOrDefault(abgeordneter, 0);
            anzahl++;
            abgeordnetenMap.put(abgeordneter, anzahl);
        });
        new HashSet<>(abgeordnetenMap.values()).stream().sorted(Comparator.comparingInt(Integer::intValue).reversed()).forEach(anzahl -> {
            abgeordnetenMap.forEach((abgeordneter, anzahlAbgeordneter) -> {
                if (anzahlAbgeordneter.equals(anzahl)) {
                    output.println("    " + abgeordneter.getLabel() + "; " + anzahl + " Mal (stellvertretende*r) Vorsitzende*r");
                }
            });
        });
        output.println();


        /* Leider ist auch dies nur für WP 20 möglich... Alas.*/
        output.println("\nTEIL 2: Oppositionsführer nach Wahlperiode");
        factory.listWahlperioden().stream().sorted(Comparator.comparingInt(Wahlperiode::getNumber)).forEach(wahlperiode -> {
            AtomicBoolean found = new AtomicBoolean(false);
            output.println(wahlperiode.getLabel());
            wahlperiode.listMandate().forEach(mandat -> {
                mandat.getAbgeordneter().listMitgliedschaften(mandat.getWahlperiode()).forEach(mitgliedschaft -> {
                    try {
                        if (mitgliedschaft.getGruppe().getLabel().equals("Haushaltsausschuss") && mitgliedschaft.getFunktion().startsWith("Vorsitzende")) {
                            mitgliedschaft.getAbgeordneter().listMitgliedschaften(wahlperiode).forEach(fraktionsMitgliedschaft -> {
                                if (fraktionsMitgliedschaft.getGruppe() instanceof Fraktion) {
                                    output.println("    " + fraktionsMitgliedschaft.getGruppe().getLabel());
                                    found.set(true);
                                }
                            });
                        }
                    } catch (BundestagException ignored) {
                    }
                });
            });
            if (!found.get()) {
                output.println("    " + "Unbekannt");
            }
        });
    }

    /**
     * Implementierung von Aufgabe 3e)
     */
    @Test
    @DisplayName("e)")
    void aufgabe3e() {
        BundestagFactory factory = BundestagFactory.newInstance();
        assertDoesNotThrow(() -> readStammdaten(factory));
        assertDoesNotThrow(() -> readAbstimmungen(factory));
        AufgabenWriter output = beginneErgebnisse(new File("Antworten/Uebung2_Antwort_e.txt"));

        factory.listFraktionen().forEach(fraktion -> {
            output.println(fraktion.getLabel());
            AtomicBoolean abstimmungenGefunden = new AtomicBoolean(false);
            fraktion.getMembers().forEach(abgeordneter -> {
                Set<Abstimmung> abstimmungen = abgeordneter.listAbstimmungen();
                if (!abstimmungen.isEmpty()) {
                    output.println("    " + abgeordneter.getLabel());
                    abstimmungen.forEach(abstimmung -> output.println("        " + abstimmung.getLabel() + ": " + abstimmung.getErgebnis()));
                    abstimmungenGefunden.set(true);
                }
            });
            if (!abstimmungenGefunden.get()) {
                output.println("    (Keine Mitglieder mit Abstimmungen bekannt)");
            }
            output.println();
        });
    }

    /**
     * Implementierung von Aufgabe 3f)
     */
    @Test
    @DisplayName("f)")
    void aufgabe3f() {
        BundestagFactory factory = BundestagFactory.newInstance();
        assertDoesNotThrow(() -> readStammdaten(factory));
        assertDoesNotThrow(() -> readProtokolle(factory));
        AufgabenWriter output = beginneErgebnisse(new File("Antworten/Uebung2_Antwort_f.txt"));

        Map<Abgeordneter, Set<Fehltag>> fehltageMap = new HashMap<>();
        factory.listFehltage().forEach(fehltag -> {
            Set<Fehltag> fehltage;
            if (fehltageMap.containsKey(fehltag.getAbgeordneter())) {
                fehltage = fehltageMap.get(fehltag.getAbgeordneter());
            } else {
                fehltage = new HashSet<>();
                fehltageMap.put(fehltag.getAbgeordneter(), fehltage);
            }
            fehltage.add(fehltag);
        });

        output.println("FEHLTAGE GESAMT:");
        fehltageMap.keySet().stream().sorted(Comparator.comparingInt(abgeordneter -> fehltageMap.get(abgeordneter).size()).reversed()).forEach(abgeordneter -> {
            output.println("    " + abgeordneter.getLabel() + ", " + fehltageMap.get(abgeordneter).size() + " Fehltage:");
            fehltageMap.get(abgeordneter).forEach(fehltag -> output.println("        " + fehltag.getDate()));
        });
        output.println();

        output.println("NACH FRAKTION:");
        Map<Fraktion, Set<Abgeordneter>> fraktionsMap = new HashMap<>();
        fehltageMap.keySet().forEach(abgeordneter -> {
            abgeordneter.listMitgliedschaften().forEach(mitgliedschaft -> {
                if (mitgliedschaft.getGruppe() instanceof Fraktion) {
                    Fraktion fraktion = (Fraktion) mitgliedschaft.getGruppe();
                    Set<Abgeordneter> abgeordnete;
                    if (fraktionsMap.containsKey(fraktion)) {
                        abgeordnete = fraktionsMap.get(fraktion);
                    } else {
                        abgeordnete = new HashSet<>();
                        fraktionsMap.put(fraktion, abgeordnete);
                    }
                    // Mengensemantik, Objekte nur einfach vorhanden.
                    abgeordnete.add(abgeordneter);
                }
            });
        });
        fraktionsMap.keySet().forEach(fraktion -> {
            output.println(fraktion.getLabel());
            fraktion.getMembers().stream().sorted((first, second) -> {
                int firstNum = 0;
                int secondNum = 0;
                if (fehltageMap.containsKey(first)) {
                    firstNum = fehltageMap.get(first).size();
                }
                if (fehltageMap.containsKey(second)) {
                    secondNum = fehltageMap.get(second).size();
                }
                return secondNum - firstNum;
            }).forEach(abgeordneter -> {
                output.println("    " + abgeordneter.getLabel() + ", " + fehltageMap.getOrDefault(abgeordneter, new HashSet<>()).size() + " Fehltage:");
                fehltageMap.getOrDefault(abgeordneter, new HashSet<>()).forEach(fehltag -> {
                    output.println("        " + fehltag.getDate());
                });
            });
        });
    }

    /**
     * Implementierung von Aufgabe 3g)
     */
    @Test
    @DisplayName("g)")
    void aufgabe3g() {
        BundestagFactory factory = BundestagFactory.newInstance();
        assertDoesNotThrow(() -> readStammdaten(factory));
        AufgabenWriter output = beginneErgebnisse(new File("Antworten/Uebung2_Antwort_g.txt"));

        Map<Abgeordneter, Long> abgeordneteLaengeMap = new HashMap<>();
        factory.listAbgeordnete().forEach(abgeordneter -> {
            Date startDate = Date.valueOf(LocalDate.now());
            Date endDate = null;

            for (Mandat mandat : abgeordneter.listMandate()) {
                if (mandat.fromDate().before(startDate)) {
                    startDate = mandat.fromDate();
                }
                if (endDate == null || (mandat.toDate() != null && mandat.toDate().before(endDate))) {
                    endDate = mandat.toDate();
                }
            }

            if (endDate == null) {
                endDate = Date.valueOf(LocalDate.now());
            }
            /* This should not be necessary. But for some reason we have to use java.sql.Date instead of something sensible like LocalDate, so here we are. */
            long days = ChronoUnit.DAYS.between(LocalDate.parse(startDate.toString()), LocalDate.parse(endDate.toString())) + 1 /* +1, da sowohl Start- als auch Enddatum dazuzählen sollen */;
            abgeordneteLaengeMap.put(abgeordneter, days);
        });
        abgeordneteLaengeMap.keySet().stream().sorted(Comparator.comparingLong(abgeordneteLaengeMap::get).reversed()).forEach(abgeordneter -> {
            output.println(abgeordneter.getLabel() + ", " + abgeordneteLaengeMap.get(abgeordneter) + " Tage");
        });
    }

    /**
     * Private Hilfsfunktion zum Ausgeben der Mandatsdominazen unter gegebenen Mandaten
     *
     * @param mandate     Die zu berücksichtigenden Mandate
     * @param wahlperiode Wahlperiode; wird nur zum Ausgeben verwendet (nicht für die Mandate beachtet)
     * @param output      AufgabenWriter, in den geschrieben werden muss
     */
    void aufgabe3h_printMandatsdominanz(Set<Mandat> mandate, Wahlperiode wahlperiode, AufgabenWriter output) {
        if (mandate.isEmpty()) {
            return;
        }

        output.println("    " + wahlperiode.getLabel());
        Map<Partei, Integer> dominanzAnzahl = new HashMap<>();
        mandate.forEach(mandat -> {
            Partei partei;
            try {
                partei = mandat.getAbgeordneter().getPartei();
            } catch (BundestagException ignored) {
                return;
            }
            int parteiZahl = dominanzAnzahl.getOrDefault(partei, 0);
            parteiZahl++;
            dominanzAnzahl.put(partei, parteiZahl);
        });
        AtomicInteger gesamtZahl = new AtomicInteger();
        dominanzAnzahl.values().forEach(gesamtZahl::addAndGet);

        if (gesamtZahl.get() == 0) {
            gesamtZahl.set(1);
        }
        dominanzAnzahl.keySet().stream().sorted(Comparator.comparingInt(dominanzAnzahl::get).reversed()).forEach(partei -> {
            output.println("        " + partei.getLabel() + ": " + (((double) dominanzAnzahl.get(partei) * 100) / gesamtZahl.get()) + "%");
        });
    }

    /**
     * Implementierung von Aufgabe 3h)
     */
    @Test
    @DisplayName("h)")
    void aufgabe3h() {
        BundestagFactory factory = BundestagFactory.newInstance();
        assertDoesNotThrow(() -> readStammdaten(factory));
        AufgabenWriter output = beginneErgebnisse(new File("Antworten/Uebung2_Antwort_h.txt"));

        List<Wahlperiode> wahlperioden = factory.listWahlperioden().stream().sorted(Comparator.comparingInt(Wahlperiode::getNumber)).collect(Collectors.toList());

        output.println("TEIL 1: NACH WAHLKREISEN (DIREKTMANDATE)");
        factory.listWahlkreise().stream().sorted(Comparator.comparingInt(Wahlkreis::getNumber)).forEach(wahlkreis -> {
            output.println(wahlkreis.getLabel());
            wahlperioden.forEach(wahlperiode -> {
                Set<Mandat> mandate = wahlkreis.getMandate(wahlperiode);
                aufgabe3h_printMandatsdominanz(mandate, wahlperiode, output);
            });
            output.println();
        });
        output.println("\n");

        output.println("TEIL 2: NACH LANDESLISTEN (LISTENMANDATE)");
        factory.listLandeslisten().stream().sorted(Comparator.comparing(Landesliste::getKuerzel)).forEach(landesliste -> {
            output.println(landesliste.getKuerzel());
            wahlperioden.forEach(wahlperiode -> {
                Set<Mandat> mandate = landesliste.getMandate(wahlperiode);
                aufgabe3h_printMandatsdominanz(mandate, wahlperiode, output);
            });
        });
    }

    /**
     * Implementierung von Aufgabe 3i)
     */
    @Test
    @DisplayName("i)")
    void aufgabe3i() {
        BundestagFactory factory = BundestagFactory.newInstance();
        assertDoesNotThrow(() -> readStammdaten(factory));
        AufgabenWriter output = beginneErgebnisse(new File("Antworten/Uebung2_Antwort_i.txt"));

        factory.listWahlperioden().stream().sorted(Comparator.comparingInt(Wahlperiode::getNumber)).forEach(wahlperiode -> {
            output.println(wahlperiode.getLabel());
            Map<Partei, Set<Abgeordneter>> praesidenten = new HashMap<>();
            Map<Partei, Set<Abgeordneter>> vizepraesidenten = new HashMap<>();
            Map<Partei, Set<Abgeordneter>> schriftfuehrer = new HashMap<>();
            factory.listAbgeordnete().forEach(abgeordneter -> {
                abgeordneter.listMitgliedschaften(wahlperiode).forEach(mitgliedschaft -> {
                    try {
                        if (mitgliedschaft.getFunktion().toLowerCase().contains("vizepräsident")) {
                            Set<Abgeordneter> abgeordnete;
                            if (vizepraesidenten.containsKey(mitgliedschaft.getAbgeordneter().getPartei())) {
                                abgeordnete = vizepraesidenten.get(mitgliedschaft.getAbgeordneter().getPartei());
                            } else {
                                abgeordnete = new HashSet<>();
                                vizepraesidenten.put(abgeordneter.getPartei(), abgeordnete);
                            }
                            abgeordnete.add(abgeordneter);
                        } else if (mitgliedschaft.getFunktion().toLowerCase().contains("präsident")) {
                            Set<Abgeordneter> abgeordnete;
                            if (praesidenten.containsKey(mitgliedschaft.getAbgeordneter().getPartei())) {
                                abgeordnete = praesidenten.get(mitgliedschaft.getAbgeordneter().getPartei());
                            } else {
                                abgeordnete = new HashSet<>();
                                praesidenten.put(abgeordneter.getPartei(), abgeordnete);
                            }
                            abgeordnete.add(abgeordneter);
                        }
                    } catch (BundestagException ignored) {
                    }
                    if (mitgliedschaft.getGruppe().getLabel().toLowerCase().contains("schriftführer")) {
                        try {
                            Set<Abgeordneter> abgeordnete;
                            if (schriftfuehrer.containsKey(mitgliedschaft.getAbgeordneter().getPartei())) {
                                abgeordnete = schriftfuehrer.get(mitgliedschaft.getAbgeordneter().getPartei());
                            } else {
                                abgeordnete = new HashSet<>();
                                schriftfuehrer.put(abgeordneter.getPartei(), abgeordnete);
                            }
                            abgeordnete.add(abgeordneter);
                        } catch (BundestagException ignored) {
                        }
                    }
                });
            });

            output.println("    Präsident*innen:");
            praesidenten.forEach((partei, abgeordnete) -> {
                output.println("        " + partei.getLabel());
                abgeordnete.forEach(abgeordneter -> output.println("            " + abgeordneter.getLabel()));
            });
            output.println("    Vizepräsident*innen:");
            vizepraesidenten.forEach((partei, abgeordnete) -> {
                output.println("        " + partei.getLabel());
                abgeordnete.forEach(abgeordneter -> output.println("            " + abgeordneter.getLabel()));
            });
            output.println("    Schriftführer*innen:");
            schriftfuehrer.forEach((partei, abgeordnete) -> {
                output.println("        " + partei.getLabel());
                abgeordnete.forEach(abgeordneter -> output.println("            " + abgeordneter.getLabel()));
            });
        });
    }

    /**
     * Hilfsklasse, um mit einem Befehl sowohl in Konsole als auch Datei schreiben zu können
     */
    private static class AufgabenWriter {
        private final PrintWriter writer;

        /**
         * Konstruktor
         *
         * @param file Datei, in die geschrieben werden soll
         */
        AufgabenWriter(File file) {
            try {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                writer = new PrintWriter(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Printed einen String
         *
         * @param s String zum Ausgeben
         */
        void println(String s) {
            System.out.println(s);
            writer.println(s);
            writer.flush();//Meh.
        }

        /**
         * Printed eine leere Zeile
         */
        void println() {
            println("");
        }
    }
}
