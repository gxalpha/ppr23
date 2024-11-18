
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.texttechnologylab.project.Stud2.data.Fraktion;
import org.texttechnologylab.project.Stud2.data.Partei;
import org.texttechnologylab.project.Stud2.database.impl.MongoDBConnectionHandler;
import org.texttechnologylab.project.Stud2.exception.BundestagException;
import org.texttechnologylab.project.Stud2.utils.StringFunctions;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.regex;

/**
 * Die Testklasse, über die die Abfragen aus Aufgabe 2 stattfinden sollen
 *
 * @author Stud2
 */
public class AbfragenTests {
    static MongoDBConnectionHandler mongoDB;
    static List<String> fraktionen;
    static List<String> parteien;

    // Alle Fraktionen zum Testen
    static Stream<Arguments> fraktionen() {
        List<Arguments> arguments = new ArrayList<>();
        for (String s : fraktionen) {
            arguments.add(Arguments.of(s));
        }
        return arguments.stream();
    }

    // Alle Parteien zum Testen
    static Stream<Arguments> parteien() {
        List<Arguments> arguments = new ArrayList<>();
        for (String s : parteien) {
            arguments.add(Arguments.of(s));
        }
        return arguments.stream();
    }

    /**
     * Wird vor jeder Abfrage durchgeführt. Erfassung der Parteien und Fraktionen sowie Verbindung mit MongoDB
     */
    @BeforeAll
    public static void init() throws IOException, ParserConfigurationException, ParseException, BundestagException, SAXException {
        mongoDB = new MongoDBConnectionHandler();
        fraktionen = mongoDB.getFactory().listFraktionen().stream().map(Fraktion::getLabel).collect(Collectors.toList());
        parteien = mongoDB.getFactory().listParteien().stream().map(Partei::getLabel).collect(Collectors.toList());
    }

    /**
     * Implementation von Abfrage 2a)
     *
     * @param obj eine Fraktion oder Partei
     */
    @ParameterizedTest
    @DisplayName("Abfrage 2(a) - gefiltert nach Fraktion/Partei")
    @MethodSource({"fraktionen", "parteien"}) // Wir führen hier den Test einfach für jede Fraktion und Partei aus
    public void abfrage2a(String obj) {

        // Überprüfung, ob es sich wirklich um eine Partei oder Fraktion handelt
        String identifier = null;

        if (parteien.contains(obj)) {
            identifier = "partei";
        }
        else if (fraktionen.contains(obj)) {
            identifier = "fraktion";
        }
        else {
            System.out.println("Übergebene Fraktion oder Partei existiert nicht");
            return;
        }

        // Datenbankabfrage
        List<Bson> query = Arrays.asList(Aggregates.match(Filters.eq(identifier, obj)),
                Aggregates.unwind("$reden"),
                Aggregates.group("$_id", Accumulators.sum("anzahlReden", 1)),
                Aggregates.lookup("Abgeordnete", "_id", "_id", "abgeordneter"),
                Aggregates.project(Projections.fields(
                        Projections.include("_id"),
                        Projections.include("abgeordneter.name.vorname"),
                        Projections.include("abgeordneter.name.nachname"),
                        Projections.include("anzahlReden"))),
                Aggregates.sort(Sorts.descending("anzahlReden")));


        AggregateIterable<Document> abgeordnete = mongoDB.aggregate(query, "Abgeordnete");

        // Ausgabe der Ergebnisse
        System.out.println(" ");
        System.out.println("*".repeat(150));
        System.out.println("Redner (".toUpperCase() + obj.toUpperCase() + ") - absteigend sortiert nach deren Anzahl an Redebeiträgen".toUpperCase());
        System.out.println("*".repeat(150));
        System.out.println(" ");

        for (Document doc : abgeordnete) {
            System.out.println("ID: " + doc.getString("_id") + " | Anzahl Redebeiträge: " + doc.getInteger("anzahlReden") + " | "
                    + ((Document) ((List<Document>) doc.get("abgeordneter")).get(0).get("name")).get("vorname") + " "
                    + ((Document) ((List<Document>) doc.get("abgeordneter")).get(0).get("name")).get("nachname"));
        }
    }

    /**
     * Implementation von Abfrage 2a) inklusive eines Datumsfilters
     *
     * @param obj   eine Fraktion oder Partei
     * @param datum ein Datum vom Format dd.MM.YYYY
     */
    @ParameterizedTest
    @DisplayName("Abfrage 2(a) - gefiltert nach Fraktion/Partei und Datum")
    // Wir führen hier den Test exemplarisch für die CDU, SPD und FDP für den 26.01.2022 aus.
    @CsvSource({"CDU,26.01.2022", "SPD,26.01.2022", "FDP,26.01.2022"})
    public void abfrage2a(String obj, String datum) throws ParseException {
        Date filterDatum = StringFunctions.toDate(datum);
        assert filterDatum != null;

        // Überprüfung, ob es sich wirklich um eine Partei oder Fraktion handelt
        String identifier = null;

        if (parteien.contains(obj)) {
            identifier = "partei";
        }
        else if (fraktionen.contains(obj)) {
            identifier = "fraktion";
        }
        else {
            System.out.println("Übergebene Fraktion oder Partei existiert nicht");
            return;
        }


        // Datenbankabfrage
        List<Bson> query = Arrays.asList(
                Aggregates.match(Filters.eq(identifier, obj)),
                Aggregates.unwind("$reden"),
                Aggregates.lookup("Reden", "reden", "_id", "details"),
                Aggregates.match(Filters.eq("details.datum", filterDatum.getTime())),
                Aggregates.group("$_id", Accumulators.sum("anzahlReden", 1)),
                Aggregates.lookup("Abgeordnete", "_id", "_id", "abgeordneter"),
                Aggregates.project(Projections.fields(
                        Projections.include("_id"),
                        Projections.include("abgeordneter.name.vorname"),
                        Projections.include("abgeordneter.name.nachname"),
                        Projections.include("anzahlReden"))),
                Aggregates.sort(Sorts.descending("anzahlReden")));


        AggregateIterable<Document> abgeordnete = mongoDB.aggregate(query, "Abgeordnete");

        // Ausgabe der Ergebnisse
        System.out.println(" ");
        System.out.println("*".repeat(150));
        System.out.println("Redner (".toUpperCase() + obj.toUpperCase() + ") AM " + datum + " - absteigend sortiert nach deren Anzahl an Redebeiträgen".toUpperCase());
        System.out.println("*".repeat(150));
        System.out.println(" ");

        for (Document doc : abgeordnete) {
            System.out.println("ID: " + doc.getString("_id") + " | Anzahl Redebeiträge: " + doc.getInteger("anzahlReden") + " | "
                    + ((Document) ((List<Document>) doc.get("abgeordneter")).get(0).get("name")).get("vorname") + " "
                    + ((Document) ((List<Document>) doc.get("abgeordneter")).get(0).get("name")).get("nachname"));
        }
    }

    /**
     * Implementation von Abfrage 2a) inklusive eines Zeitraumfilters
     *
     * @param obj eine Fraktion oder Partei
     * @param von der Beginn des Zeitraums im Format dd.MM.YYYY
     * @param bis das Ende des Zeitraums im Format dd.MM.YYYY
     */
    @ParameterizedTest
    @DisplayName("Abfrage 2(a) - gefiltert nach Fraktion/Partei und Zeitraum")
    // Wir führen hier den Test exemplarisch für die CDU, SPD und FDP vom 26.01.2022 bis 26.07.2022 aus:
    @CsvSource({"CDU,26.01.2022,26.07.2022", "SPD,26.01.2022,26.07.2022", "FDP,26.01.2022,26.07.2022"})
    public void abfrage2a(String obj, String von, String bis) throws ParseException {
        Date filterDatumVon = StringFunctions.toDate(von);
        Date filterDatumBis = StringFunctions.toDate(bis);

        assert filterDatumVon != null;
        assert filterDatumBis != null;

        // Überprüfung, ob es sich wirklich um eine Partei oder Fraktion handelt
        String identifier = null;

        if (parteien.contains(obj)) {
            identifier = "partei";
        }
        else if (fraktionen.contains(obj)) {
            identifier = "fraktion";
        }
        else {
            System.out.println("Übergebene Fraktion oder Partei existiert nicht");
            return;
        }

        // Datenbankabfrage
        List<Bson> query = Arrays.asList(
                Aggregates.match(Filters.eq(identifier, obj)),
                Aggregates.unwind("$reden"),
                Aggregates.lookup("Reden", "reden", "_id", "details"),
                Aggregates.match(Filters.gte("details.datum", filterDatumVon.getTime())),
                Aggregates.match(Filters.lte("details.datum", filterDatumBis.getTime())),
                Aggregates.group("$_id", Accumulators.sum("anzahlReden", 1)),
                Aggregates.lookup("Abgeordnete", "_id", "_id", "abgeordneter"),
                Aggregates.project(Projections.fields(
                        Projections.include("_id"),
                        Projections.include("abgeordneter.name.vorname"),
                        Projections.include("abgeordneter.name.nachname"),
                        Projections.include("anzahlReden"))),
                Aggregates.sort(Sorts.descending("anzahlReden")));


        AggregateIterable<Document> abgeordnete = mongoDB.aggregate(query, "Abgeordnete");

        // Ausgabe der Ergebnisse
        System.out.println(" ");
        System.out.println("*".repeat(150));
        System.out.println("Redner (".toUpperCase() + obj.toUpperCase() + ") VON " + von + " BIS " + bis + " - absteigend sortiert nach deren Anzahl an Redebeiträgen".toUpperCase());
        System.out.println("*".repeat(150));
        System.out.println(" ");

        for (Document doc : abgeordnete) {
            System.out.println("ID: " + doc.getString("_id") + " | Anzahl Redebeiträge: " + doc.getInteger("anzahlReden") + " | "
                    + ((Document) ((List<Document>) doc.get("abgeordneter")).get(0).get("name")).get("vorname") + " "
                    + ((Document) ((List<Document>) doc.get("abgeordneter")).get(0).get("name")).get("nachname"));
        }
    }

    /**
     * Implementation von Abfrage 2b)
     *
     * @param suchstring der String, dessen Häufigkeit in den Reden bestimmt werden soll
     */
    @ParameterizedTest
    @DisplayName("Abfrage 2(b)")
    @ValueSource(strings = {"Ukraine", "Russland", "Ampel"})
    public void abfrage2b(String suchstring){

        // Datenbankabfrage
        List<Bson> query = Arrays.asList(
                Aggregates.unwind("$text"),
                Aggregates.match(regex("text", ".*?" + suchstring + ".*?")),
                Aggregates.group("$_id", Accumulators.sum("vorkommen", 1)),
                Aggregates.lookup("Reden", "_id", "_id", "details"),
                Aggregates.project(Projections.fields(
                        Projections.include("_id"),
                        Projections.include("details.datum"), // in ms
                        Projections.include("details.rednerID"),
                        Projections.include("vorkommen")
                )),
                Aggregates.sort(Sorts.descending("vorkommen"))
        );

        AggregateIterable<Document> reden = mongoDB.aggregate(query, "Reden");

        // Ausgabe der Ergebnisse
        System.out.println(" ");
        System.out.println("*".repeat(150));
        System.out.println("Häufigkeit des Wortes ".toUpperCase() + suchstring + " pro Rede - absteigend sortiert nach deren Anzahl".toUpperCase());
        System.out.println("*".repeat(150));
        System.out.println(" ");

        for (Document doc : reden) {
            System.out.println("ID der Rede: " + doc.getString("_id") + " | Anzahl des Wortes " + suchstring +
                    ": " + doc.getInteger("vorkommen") + " | ID des Redners: "
                    + ((List<Document>) doc.get("details")).get(0).get("rednerID"));
        }
    }

    /**
     * Implementation von Abfrage 2b) inklusive eines Datumsfilters
     *
     * @param suchstring der String, dessen Häufigkeit in den Reden bestimmt werden soll
     * @param datum      das Datum, nach dem gefiltert werden soll
     */
    @ParameterizedTest
    @DisplayName("Abfrage 2(b) - gefiltert nach einem Datum")
    @CsvSource({"Ukraine,27.04.2022", "Russland,27.04.2022", "Ampel,27.04.2022"})
    public void abfrage2b(String suchstring, String datum) throws ParseException {

        Date filterDatum = StringFunctions.toDate(datum);
        assert filterDatum != null;

        // Datenbankabfrage
        List<Bson> query = Arrays.asList(
                Aggregates.match(Filters.eq("datum", filterDatum.getTime())),
                Aggregates.unwind("$text"),
                Aggregates.match(regex("text", ".*?" + suchstring + ".*?")),
                Aggregates.group("$_id", Accumulators.sum("vorkommen", 1)),
                Aggregates.lookup("Reden", "_id", "_id", "details"),
                Aggregates.project(Projections.fields(
                        Projections.include("_id"),
                        Projections.include("details.datum"), // in ms
                        Projections.include("details.rednerID"),
                        Projections.include("vorkommen")
                )),
                Aggregates.sort(Sorts.descending("vorkommen"))
        );

        AggregateIterable<Document> reden = mongoDB.aggregate(query, "Reden");

        // Ausgabe der Ergebnisse
        System.out.println(" ");
        System.out.println("*".repeat(150));
        System.out.println("Häufigkeit des Wortes ".toUpperCase() + suchstring + " pro Rede am ".toUpperCase()
                + datum + " - absteigend sortiert nach deren Anzahl".toUpperCase());
        System.out.println("*".repeat(150));
        System.out.println(" ");

        for (Document doc : reden) {
            System.out.println("ID der Rede: " + doc.getString("_id") + " | Anzahl des Wortes " + suchstring +
                    ": " + doc.getInteger("vorkommen") + " | ID des Redners: "
                    + ((List<Document>) doc.get("details")).get(0).get("rednerID"));
        }
    }

    /**
     * Implementation von Abfrage 2b) inklusive eines Zeitraumfilters
     *
     * @param suchstring der String, dessen Häufigkeit in den Reden bestimmt werden soll
     * @param von        der Beginn des Zeitraums im Format dd.MM.YYYY
     * @param bis        das Ende des Zeitraums im Format dd.MM.YYYY
     */
    @ParameterizedTest
    @DisplayName("Abfrage 2(b) - gefiltert nach einem Zeitraum")
    // Wir testen hier wieder nur exemplarisch für einen zufälligen Zeitraum:
    @CsvSource({"Ukraine,26.01.2022,26.05.2022", "Russland,26.01.2022,26.05.2022", "Ampel,26.01.2022,26.05.2022"})
    public void abfrage2b(String suchstring, String von, String bis) throws ParseException {
        Date filterDatumVon = StringFunctions.toDate(von);
        Date filterDatumBis = StringFunctions.toDate(bis);

        assert filterDatumVon != null;
        assert filterDatumBis != null;

        // Datenbankabfrage
        List<Bson> query = Arrays.asList(
                Aggregates.match(Filters.gte("datum", filterDatumVon.getTime())),
                Aggregates.match(Filters.lte("datum", filterDatumBis.getTime())),
                Aggregates.unwind("$text"),
                Aggregates.match(regex("text", ".*?" + suchstring + ".*?")),
                Aggregates.group("$_id", Accumulators.sum("vorkommen", 1)),
                Aggregates.lookup("Reden", "_id", "_id", "details"),
                Aggregates.project(Projections.fields(
                        Projections.include("_id"),
                        Projections.include("details.datum"), // in ms
                        Projections.include("details.rednerID"),
                        Projections.include("vorkommen")
                )),
                Aggregates.sort(Sorts.descending("vorkommen"))
        );

        AggregateIterable<Document> reden = mongoDB.aggregate(query, "Reden");

        // Ausgabe der Ergebnisse
        System.out.println(" ");
        System.out.println("*".repeat(150));
        System.out.println("Häufigkeit des Wortes ".toUpperCase() + suchstring + " pro Rede von ".toUpperCase()
                + von + " BIS " + bis + " - absteigend sortiert nach deren Anzahl".toUpperCase());
        System.out.println("*".repeat(150));
        System.out.println(" ");

        for (Document doc : reden) {
            System.out.println("ID der Rede: " + doc.getString("_id") + " | Anzahl des Wortes " + suchstring +
                    ": " + doc.getInteger("vorkommen") + " | ID des Redners: "
                    + ((List<Document>) doc.get("details")).get(0).get("rednerID"));
        }
    }

    /**
     * Implementation von Abfrage 2c)
     *
     * @param obj die Fraktion oder Partei, nach der gefiltert werden soll
     */
    @ParameterizedTest
    @DisplayName("Abfrage 2(c) - gefiltert nach Fraktion/Partei")
    @MethodSource({"fraktionen", "parteien"})
    public void abfrage2c(String obj) {

        // Überprüfung, ob es sich wirklich um eine Partei oder Fraktion handelt
        String identifier = null;

        if (parteien.contains(obj)) {
            identifier = "partei";
        }
        else if (fraktionen.contains(obj)) {
            identifier = "fraktion";
        }
        else {
            System.out.println("Übergebene Fraktion oder Partei existiert nicht");
            return;
        }

        // Datenbankabfrage
        List<Bson> query = Arrays.asList(
                Aggregates.group("$rednerID", Accumulators.avg("durchschnittlicheWorteProRede", "$anzahlWorte")),
                Aggregates.lookup("Abgeordnete", "_id", "_id", "abgeordneter"),
                Aggregates.match(Filters.eq("abgeordneter." + identifier, obj)),
                Aggregates.project(Projections.fields(
                        Projections.include("abgeordneter.name.vorname"),
                        Projections.include("abgeordneter.name.nachname"),
                        Projections.include("durchschnittlicheWorteProRede")
                )),
                Aggregates.sort(Sorts.descending("durchschnittlicheWorteProRede"))
        );

        AggregateIterable<Document> abgeordnete = mongoDB.aggregate(query, "Reden");

        // Ausgabe der Ergebnisse
        System.out.println(" ");
        System.out.println("*".repeat(150));
        System.out.println("REDNER (" + obj + ") - absteigend sortiert nach deren durchschnittlicher Redelänge".toUpperCase());
        System.out.println("*".repeat(150));
        System.out.println(" ");

        for (Document doc : abgeordnete) {
            System.out.println("ID: " + doc.getString("_id") + " | Durchschnittliche Anzahl an Worten: "
                    + doc.getDouble("durchschnittlicheWorteProRede").intValue() + " | "
                    + ((Document) ((List<Document>) doc.get("abgeordneter")).get(0).get("name")).get("vorname") + " "
                    + ((Document) ((List<Document>) doc.get("abgeordneter")).get(0).get("name")).get("nachname"));
        }
    }

    /**
     * Implementation von Abfrage 2c) inklusive eines Datumsfilters
     *
     * @param obj   die Fraktion oder Partei, nach der gefiltert werden soll
     * @param datum das zu filternde Datum im Format dd.MM.YYYY
     */
    @ParameterizedTest
    @DisplayName("Abfrage 2(c) - gefiltert nach Fraktion/Partei und einem Datum")
    // Wir führen hier den Test exemplarisch für die CDU, SPD und FDP für den 26.01.2022 aus.
    @CsvSource({"CDU,26.01.2022", "SPD,26.01.2022", "FDP,26.01.2022"})
    public void abfrage2c(String obj, String datum) throws ParseException {

        Date filterDatum = StringFunctions.toDate(datum);
        assert filterDatum != null;

        // Überprüfung, ob es sich wirklich um eine Partei oder Fraktion handelt
        String identifier = null;

        if (parteien.contains(obj)) {
            identifier = "partei";
        }
        else if (fraktionen.contains(obj)) {
            identifier = "fraktion";
        }
        else {
            System.out.println("Übergebene Fraktion oder Partei existiert nicht");
            return;
        }

        // Datenbankabfrage
        List<Bson> query = Arrays.asList(
                Aggregates.match(Filters.eq("datum", filterDatum.getTime())),
                Aggregates.group("$rednerID", Accumulators.avg("durchschnittlicheWorteProRede", "$anzahlWorte")),
                Aggregates.lookup("Abgeordnete", "_id", "_id", "abgeordneter"),
                Aggregates.match(Filters.eq("abgeordneter." + identifier, obj)),
                Aggregates.project(Projections.fields(
                        Projections.include("abgeordneter.name.vorname"),
                        Projections.include("abgeordneter.name.nachname"),
                        Projections.include("durchschnittlicheWorteProRede")
                )),
                Aggregates.sort(Sorts.descending("durchschnittlicheWorteProRede"))
        );

        AggregateIterable<Document> abgeordnete = mongoDB.aggregate(query, "Reden");

        // Ausgabe der Ergebnisse
        System.out.println(" ");
        System.out.println("*".repeat(150));
        System.out.println("REDNER (" + obj + ") AM " + datum + " - absteigend sortiert nach deren durchschnittlicher Redelänge".toUpperCase());
        System.out.println("*".repeat(150));
        System.out.println(" ");

        for (Document doc : abgeordnete) {
            System.out.println("ID: " + doc.getString("_id") + " | Durchschnittliche Anzahl an Worten: "
                    + doc.getDouble("durchschnittlicheWorteProRede").intValue() + " | "
                    + ((Document) ((List<Document>) doc.get("abgeordneter")).get(0).get("name")).get("vorname") + " "
                    + ((Document) ((List<Document>) doc.get("abgeordneter")).get(0).get("name")).get("nachname"));
        }
    }

    /**
     * Implementation von Abfrage 2c) - inklusive eines Zeitraumfilters
     *
     * @param obj   die Fraktion oder Partei, nach der gefiltert werden soll
     * @param von        der Beginn des Zeitraums im Format dd.MM.YYYY
     * @param bis        das Ende des Zeitraums im Format dd.MM.YYYY
     */
    @ParameterizedTest
    @DisplayName("Abfrage 2(c) - gefiltert nach Fraktion/Partei und einem Zeitraum")
    // Wir führen hier den Test exemplarisch für die CDU, SPD und FDP von 26.01.2022 bis 26.07.2022 aus.
    @CsvSource({"CDU,26.01.2022,26.07.2022", "SPD,26.01.2022,26.07.2022", "FDP,26.01.2022,26.07.2022"})
    public void abfrage2c(String obj, String von, String bis) throws ParseException {

        Date filterDatumVon = StringFunctions.toDate(von);
        Date filterDatumBis = StringFunctions.toDate(bis);

        assert filterDatumVon != null;
        assert filterDatumBis != null;

        // Überprüfung, ob es sich wirklich um eine Partei oder Fraktion handelt
        String identifier = null;

        if (parteien.contains(obj)) {
            identifier = "partei";
        }
        else if (fraktionen.contains(obj)) {
            identifier = "fraktion";
        }
        else {
            System.out.println("Übergebene Fraktion oder Partei existiert nicht");
            return;
        }

        // Datenbankabfrage
        List<Bson> query = Arrays.asList(
                Aggregates.match(Filters.gte("datum", filterDatumVon.getTime())),
                Aggregates.match(Filters.lte("datum", filterDatumBis.getTime())),
                Aggregates.group("$rednerID", Accumulators.avg("durchschnittlicheWorteProRede", "$anzahlWorte")),
                Aggregates.lookup("Abgeordnete", "_id", "_id", "abgeordneter"),
                Aggregates.match(Filters.eq("abgeordneter." + identifier, obj)),
                Aggregates.project(Projections.fields(
                        Projections.include("abgeordneter.name.vorname"),
                        Projections.include("abgeordneter.name.nachname"),
                        Projections.include("durchschnittlicheWorteProRede")
                )),
                Aggregates.sort(Sorts.descending("durchschnittlicheWorteProRede"))
        );

        AggregateIterable<Document> abgeordnete = mongoDB.aggregate(query, "Reden");

        // Ausgabe der Ergebnisse
        System.out.println(" ");
        System.out.println("*".repeat(150));
        System.out.println("REDNER (" + obj + ") VON " + von + " BIS " + bis + " - absteigend sortiert nach deren durchschnittlicher Redelänge".toUpperCase());
        System.out.println("*".repeat(150));
        System.out.println(" ");

        for (Document doc : abgeordnete) {
            System.out.println("ID: " + doc.getString("_id") + " | Durchschnittliche Anzahl an Worten: "
                    + doc.getDouble("durchschnittlicheWorteProRede").intValue() + " | "
                    + ((Document) ((List<Document>) doc.get("abgeordneter")).get(0).get("name")).get("vorname") + " "
                    + ((Document) ((List<Document>) doc.get("abgeordneter")).get(0).get("name")).get("nachname"));
        }
    }


    /**
     * Implementation von Abfrage 2d)
     */
    @Test
    @DisplayName("Abfrage 2d)")
    public void abfrage2d() {

        List<Bson> query = Arrays.asList(
                Aggregates.project(Projections.fields(
                        Projections.include("sitzungsnummer"),
                        Projections.include("datum"),
                        // Dauer in Stunden berechnen
                        Projections.computed("dauer", new Document("$divide", Arrays.asList(new Document("$subtract", Arrays.asList("$ende", "$beginn")), 3600000)))
                )),
                Aggregates.sort(Sorts.descending("dauer"))
        );

        AggregateIterable<Document> sitzungen = mongoDB.aggregate(query, "Sitzungen");

        // Ausgabe der Ergebnisse
        System.out.println(" ");
        System.out.println("*".repeat(150));
        System.out.println("Sitzungen - absteigend sortiert nach deren Dauer".toUpperCase());
        System.out.println("*".repeat(150));
        System.out.println(" ");

        for (Document doc : sitzungen) {
            Date datum = new Date(doc.getLong("datum"));
            System.out.println("Datum des Sitzungsbeginns: " + datum + " | Sitzungsnummer: " + doc.get("sitzungsnummer") + " | Dauer in Stunden: " + doc.get("dauer"));
        }
    }

    /**
     * Implementation von Abfrage 2d) inklusive eines Datumsfilters
     *
     * @param datum das Datum im Format dd.MM.YYYY, nach dem gefiltert werden soll
     */
    @ParameterizedTest
    @DisplayName("Abfrage 2d) - gefiltert nach einem Datum")
    @ValueSource(strings = {"26.10.2021"}) // exemplarisch die erste Sitzung
    public void abfrage2d(String datum) throws ParseException {

        Date filterDatum = StringFunctions.toDate(datum);
        assert filterDatum != null;

        // Datenbankabfrage
        List<Bson> query = Arrays.asList(
                Aggregates.match(Filters.eq("datum", filterDatum.getTime())),
                Aggregates.project(Projections.fields(
                        Projections.include("sitzungsnummer"),
                        Projections.include("datum"),
                        // Dauer in Stunden berechnen
                        Projections.computed("dauer", new Document("$divide", Arrays.asList(new Document("$subtract", Arrays.asList("$ende", "$beginn")), 3600000)))
                )),
                Aggregates.sort(Sorts.descending("dauer"))
        );

        AggregateIterable<Document> sitzungen = mongoDB.aggregate(query, "Sitzungen");

        // Ausgabe der Ergebnisse
        System.out.println(" ");
        System.out.println("*".repeat(150));
        System.out.println("Sitzung am ".toUpperCase() + datum + " und dessen Dauer (falls Sitzung nicht existiert - kein eintrag)".toUpperCase());
        System.out.println("*".repeat(150));
        System.out.println(" ");

        for (Document doc : sitzungen) {
            Date d = new Date(doc.getLong("datum"));
            System.out.println("Datum des Sitzungsbeginns: " + d + " | Sitzungsnummer: " + doc.get("sitzungsnummer") + " | Dauer in Stunden: " + doc.get("dauer"));
        }
    }

    /**
     * Implementation von Abfrage 2d) inklusive eines Zeitraumfilters
     *
     * @param von der Beginn des Zeitraums im Format dd.MM.YYYY
     * @param bis das Ende des Zeitraums im Format dd.MM.YYYY
     */
    @ParameterizedTest
    @DisplayName("Abfrage 2d) - gefiltert nach einem Zeitraum")
    @CsvSource ({"26.10.2021,17.01.2022"}) // die ersten 12 Sitzungen sollten herauskommen
    public void abfrage2d(String von, String bis) throws ParseException {

        Date filterDatumVon = StringFunctions.toDate(von);
        Date filterDatumBis = StringFunctions.toDate(bis);
        assert filterDatumBis != null;
        assert filterDatumVon != null;

        // Datenbankabfrage
        List<Bson> query = Arrays.asList(
                Aggregates.match(Filters.gte("datum", filterDatumVon.getTime())),
                Aggregates.match(Filters.lte("datum", filterDatumBis.getTime())),
                Aggregates.project(Projections.fields(
                        Projections.include("sitzungsnummer"),
                        Projections.include("datum"),
                        // Dauer in Stunden berechnen
                        Projections.computed("dauer", new Document("$divide", Arrays.asList(new Document("$subtract", Arrays.asList("$ende", "$beginn")), 3600000)))
                )),
                Aggregates.sort(Sorts.descending("dauer"))
        );

        AggregateIterable<Document> sitzungen = mongoDB.aggregate(query, "Sitzungen");

        // Ausgabe der Ergebnisse
        System.out.println(" ");
        System.out.println("*".repeat(150));
        System.out.println("Sitzungen vom ".toUpperCase() + von + " BIS " + bis + " - geordnet nach Dauer".toUpperCase());
        System.out.println("*".repeat(150));
        System.out.println(" ");

        for (Document doc : sitzungen) {
            Date d = new Date(doc.getLong("datum"));
            System.out.println("Datum des Sitzungsbeginns: " + d + " | Sitzungsnummer: " + doc.get("sitzungsnummer") + " | Dauer in Stunden: " + doc.get("dauer"));
        }
    }
}
