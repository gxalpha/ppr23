package org.texttechnologylab.project.Stud1.Uebung3;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.texttechnologylab.project.Stud1.data.impl.Abgeordneter_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.Rede_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.Sitzung_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.database.MongoDBConnectionHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


@DisplayName("Aufgabe 2")
public class AbfragenTest {
    private MongoDBConnectionHandler createConnectionHandler() throws IOException {
        return new MongoDBConnectionHandler(AbfragenTest.class.getClassLoader().getResourceAsStream("config.ini"));
    }


    private static List<Arguments> aufgabe2aSource() {
        return List.of(
                Arguments.of(null, null),
                Arguments.of("CDU", null),
                Arguments.of(null, "Fraktion der Sozialdemokratischen Partei Deutschlands"),
                Arguments.of("SSW", "Fraktionslos")
        );
    }

    @ParameterizedTest
    @MethodSource("aufgabe2aSource")
    @DisplayName("a)")
    public void aufgabe2a(String partei, String fraktion) throws IOException {
        MongoDBConnectionHandler handler = createConnectionHandler();
        MongoDatabase database = handler.getDatabase();
        AufgabenWriter writer = beginneErgebnisse("Antworten/aufgabe2a_" + partei + "_" + fraktion + ".txt");

        List<Bson> aggregates = new ArrayList<>(List.of(
                Aggregates.group(
                        "$" + Rede_MongoDB_Impl.Keys.REDNER,
                        Accumulators.sum("reden_count", 1)
                ),
                Aggregates.lookup(Abgeordneter_MongoDB_Impl.Keys.COLLECTION_NAME, "_id", Abgeordneter_MongoDB_Impl.Keys.ID, "redner_attach"),
                Aggregates.unwind("$redner_attach"),
                Aggregates.sort(Sorts.descending("reden_count")),
                Aggregates.project(
                        Projections.fields(
                                new Document("Redenanzahl", "$reden_count"),
                                new Document("Vorname", "$redner_attach." + Abgeordneter_MongoDB_Impl.Keys.VORNAME),
                                new Document("Nachname", "$redner_attach." + Abgeordneter_MongoDB_Impl.Keys.NACHNAME),
                                new Document("Partei", "$redner_attach." + Abgeordneter_MongoDB_Impl.Keys.PARTEI),
                                new Document("Fraktion", "$redner_attach." + Abgeordneter_MongoDB_Impl.Keys.FRAKTION)
                        )
                )
        ));
        if (partei != null) {
            aggregates.add(Aggregates.match(Filters.eq("Partei", partei)));
        }
        if (fraktion != null) {
            aggregates.add(Aggregates.match(Filters.eq("Fraktion", fraktion)));
        }
        database.getCollection(Rede_MongoDB_Impl.Keys.COLLECTION_NAME).aggregate(aggregates).forEach((Consumer<? super Document>) writer::println);
    }

    private static List<String> aufgabe2bSource() {
        return List.of(
                "Ukraine",
                "Russland",
                "Ampel"
        );
    }

    @ParameterizedTest
    @MethodSource("aufgabe2bSource")
    @DisplayName("b)")
    public void aufgabe2b(String text) throws IOException {
        MongoDBConnectionHandler handler = createConnectionHandler();
        MongoDatabase database = handler.getDatabase();
        AufgabenWriter writer = beginneErgebnisse("Antworten/aufgabe2b_" + text + ".txt");

        MongoCollection<Document> collection = database.getCollection(Rede_MongoDB_Impl.Keys.COLLECTION_NAME);
        collection.createIndex(Indexes.text(Rede_MongoDB_Impl.Keys.TEXT));

        Bson query = new Document("$" + Rede_MongoDB_Impl.Keys.TEXT, new Document("$search", "\"" + text + "\""));


        collection.find(query).projection(new Document("search_score", new Document("$meta", "textScore"))).sort(Sorts.metaTextScore("search_score")).forEach((Consumer<? super Document>) writer::println);
    }

    @Test
    @DisplayName("c) Abgeordnete")
    public void aufgabe2c_abgeordnete() throws IOException {
        MongoDBConnectionHandler handler = createConnectionHandler();
        MongoDatabase database = handler.getDatabase();
        AufgabenWriter writer = beginneErgebnisse("Antworten/aufgabe2c_abgeordnete.txt");


        MongoCollection<Document> collection = database.getCollection(Rede_MongoDB_Impl.Keys.COLLECTION_NAME);
        collection.aggregate(List.of(
                Aggregates.group(
                        "$" + Rede_MongoDB_Impl.Keys.REDNER,
                        Accumulators.avg("words_avg", "$" + Rede_MongoDB_Impl.Keys.TEXT_LENGTH)
                ),
                Aggregates.lookup(Abgeordneter_MongoDB_Impl.Keys.COLLECTION_NAME, "_id", Abgeordneter_MongoDB_Impl.Keys.ID, "redner_info"),
                Aggregates.unwind("$redner_info"),
                Aggregates.project(
                        Projections.fields(
                                new Document("Vorname", "$redner_info." + Abgeordneter_MongoDB_Impl.Keys.VORNAME),
                                new Document("Nachname", "$redner_info." + Abgeordneter_MongoDB_Impl.Keys.NACHNAME),
                                new Document("Partei", "$redner_info." + Abgeordneter_MongoDB_Impl.Keys.PARTEI),
                                new Document("Fraktion", "$redner_info." + Abgeordneter_MongoDB_Impl.Keys.FRAKTION),
                                new Document("Durchschnittliche Länge", "$words_avg"),
                                Projections.exclude("_id")
                        )
                )
        )).forEach((Consumer<? super Document>) writer::println);
    }

    /**
     * Alle Fraktionen werden ausgegeben, Parameter ist also nicht nötig.
     */
    @Test
    @DisplayName("c) Fraktionen")
    public void aufgabe2c_fraktionen() throws IOException {
        MongoDBConnectionHandler handler = createConnectionHandler();
        MongoDatabase database = handler.getDatabase();
        AufgabenWriter writer = beginneErgebnisse("Antworten/aufgabe2c_fraktionen.txt");


        MongoCollection<Document> collection = database.getCollection(Rede_MongoDB_Impl.Keys.COLLECTION_NAME);
        collection.aggregate(List.of(
                Aggregates.lookup(Abgeordneter_MongoDB_Impl.Keys.COLLECTION_NAME, Rede_MongoDB_Impl.Keys.REDNER, Abgeordneter_MongoDB_Impl.Keys.ID, "redner_info"),
                Aggregates.unwind("$redner_info"),
                Aggregates.project(
                        Projections.fields(
                                new Document("Fraktion", "$redner_info." + Abgeordneter_MongoDB_Impl.Keys.FRAKTION),
                                new Document("length", "$" + Rede_MongoDB_Impl.Keys.TEXT_LENGTH)
                        )
                ),
                Aggregates.group(
                        "$Fraktion",
                        Accumulators.avg("Durchschnittliche Länge", "$length")
                )
        )).forEach((Consumer<? super Document>) writer::println);
    }

    /**
     * Alle Parteien werden ausgegeben, Parameter ist also nicht nötig.
     */
    @Test
    @DisplayName("c) Parteien")
    public void aufgabe2c_parteien() throws IOException {
        MongoDBConnectionHandler handler = createConnectionHandler();
        MongoDatabase database = handler.getDatabase();
        AufgabenWriter writer = beginneErgebnisse("Antworten/aufgabe2c_parteien.txt");


        MongoCollection<Document> collection = database.getCollection(Rede_MongoDB_Impl.Keys.COLLECTION_NAME);
        collection.aggregate(List.of(
                Aggregates.lookup(Abgeordneter_MongoDB_Impl.Keys.COLLECTION_NAME, Rede_MongoDB_Impl.Keys.REDNER, Abgeordneter_MongoDB_Impl.Keys.ID, "redner_info"),
                Aggregates.unwind("$redner_info"),
                Aggregates.project(
                        Projections.fields(
                                new Document("Partei", "$redner_info." + Abgeordneter_MongoDB_Impl.Keys.PARTEI),
                                new Document("length", "$" + Rede_MongoDB_Impl.Keys.TEXT_LENGTH)
                        )
                ),
                Aggregates.group(
                        "$Partei",
                        Accumulators.avg("Durchschnittliche Länge", "$length")
                )
        )).forEach((Consumer<? super Document>) writer::println);
    }

    /**
     * Abfrage 2d)
     */
    @Test
    @DisplayName("d)")
    public void aufgabe2d() throws IOException {
        MongoDBConnectionHandler handler = createConnectionHandler();
        MongoDatabase database = handler.getDatabase();
        AufgabenWriter writer = beginneErgebnisse("Antworten/aufgabe2d.txt");

        MongoCollection<Document> collection = database.getCollection(Sitzung_MongoDB_Impl.Keys.COLLECTION_NAME);
        collection.aggregate(List.of(
                Aggregates.sort(Sorts.descending("dauer")),
                Aggregates.project(
                        Projections.fields(
                                Projections.include("datum", "dauer", "rede_ids"),
                                Projections.exclude("_id")
                        )
                )
        )).forEach((Consumer<? super Document>) writer::println);
    }

    /**
     * Private Hilfsfunktion zum Erstellen des Dateien-Schreibers und schönen Formatieren der Konsole
     *
     * @param fileName Ergebnis-Datei
     * @return AufgabenWriter, in die Ergebnisse geschrieben werden sollen
     */
    private AufgabenWriter beginneErgebnisse(String fileName) {
        System.out.println();
        System.out.println("================== BEGIN ERGEBNISSE ==================");
        System.out.println();
        return new AufgabenWriter(new File(fileName));
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
         * Printed ein Document
         *
         * @param document Document zum Ausgeben
         */
        void println(Document document) {
            println(document.toString());
        }

        /**
         * Printed eine leere Zeile
         */
        void println() {
            println("");
        }
    }
}
