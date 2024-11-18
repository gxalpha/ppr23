package org.texttechnologylab.project.Stud2.helper;

import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.bson.BsonMaximumSizeExceededException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.texttechnologylab.DockerUnifiedUIMAInterface.DUUIComposer;
import org.texttechnologylab.DockerUnifiedUIMAInterface.driver.DUUIDockerDriver;
import org.texttechnologylab.DockerUnifiedUIMAInterface.lua.LuaConsts;
import org.texttechnologylab.project.Stud2.data.impl.mongoDB.RedeMongoDBImpl;
import org.texttechnologylab.project.Stud2.database.MongoDBConnectionHandler;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enthält Hilfsmethoden für das Natural Language Processing
 *
 * @author Stud2
 */
public class NLPHelper {
    private final DUUIComposer composer;
    private final MongoDBConnectionHandler mongoDB;
    private boolean continueNLP = true;
    private String nlpProgress = "0.00%";

    private long startTimeNLP = 0;

    /**
     * Konstruktor für einen NLPHelper
     *
     * @param mongoDB der Zugang zur Datenbank über einen MongoDBConnectionHandler
     */
    public NLPHelper(MongoDBConnectionHandler mongoDB) throws IOException, URISyntaxException, UIMAException, SAXException, CompressorException {

        this.mongoDB = mongoDB;

        int workers = 1;

        // Composer für das NLP initialisieren
        this.composer = new DUUIComposer()
                .withSkipVerification(true)
                .withLuaContext(LuaConsts.getJSON())
                .withWorkers(workers);

        DUUIDockerDriver dockerDriver = new DUUIDockerDriver();

        composer.addDriver(dockerDriver);

        // Bitte die folgenden beiden Docker-Images über "docker pull" vorher herunterladen!
        composer.add(new DUUIDockerDriver.Component("docker.texttechnologylab.org/textimager-duui-spacy-single-de_core_news_sm:0.1.6")
                .withScale(workers)
                .build());
        composer.add(new DUUIDockerDriver.Component("docker.texttechnologylab.org/gervader_duui:1.0.2")
                .withParameter("selection", "text")
                .withScale(workers)
                .build());
    }

    /**
     * Führt das NLP einer Rede durch und speichert das CAS sowie weitere ausgewählte Informationen in der MongoDB ab
     *
     * @param rede die zu analysierende Rede
     */
    public void analyse(RedeMongoDBImpl rede) throws Exception {

        // Analyse der Rede
        JCas jCas = rede.toCAS();

        this.composer.run(jCas, "Analysis of speech with the following ID: " + rede.getID());

        rede.setJCas(jCas);

        // Annotationen der analysierten Rede serialisieren und in der MongoDB ablegen
        ByteArrayOutputStream xmi = new ByteArrayOutputStream();
        XmiCasSerializer.serialize(jCas.getCas(), xmi);

        Document redeNLP = rede.toDoc();
        redeNLP.put("cas", String.valueOf(xmi));

        // Spezifische Informationen neben dem CAS nochmal zusätzlich zur Rede abspeichern

        // Token
        redeNLP.put("token", rede.getToken().stream().map(token -> {

            Document doc = new Document();
            doc.put("text", token.getText());
            doc.put("lemmaValue", token.getLemmaValue());
            doc.put("formValue", token.getFormValue());
            doc.put("stemValue", token.getStemValue());
            doc.put("posValue", token.getPosValue());
            doc.put("begin", token.getBegin());
            doc.put("end", token.getEnd());
            return doc;

        }).collect(Collectors.toList()));

        // Sentences
        redeNLP.put("sentences", rede.getSentences().stream().map(sentence -> {
            Document doc = new Document();
            doc.put("text", sentence.getCoveredText());
            doc.put("begin", sentence.getBegin());
            doc.put("end", sentence.getEnd());
            return doc;

        }).collect(Collectors.toList()));

        // Parts of speech
        redeNLP.put("POS", rede.getPOS().stream().map(pos -> {
            Document doc = new Document();
            doc.put("text", pos.getCoveredText());
            doc.put("posValue", pos.getPosValue());
            doc.put("begin", pos.getBegin());
            doc.put("end", pos.getEnd());
            return doc;

        }).collect(Collectors.toList()));

        // Dependencies
        redeNLP.put("dependencies", rede.getDependency().stream().map(dependency -> {
            Document doc = new Document();
            doc.put("dependencyType", dependency.getDependencyType());
            doc.put("dependent", dependency.getDependent().getText());
            doc.put("governor", dependency.getGovernor().getText());
            doc.put("flavor", dependency.getFlavor());
            doc.put("begin", dependency.getBegin());
            doc.put("end", dependency.getEnd());
            return doc;

        }).collect(Collectors.toList()));

        // Named Entities
        redeNLP.put("namedEntities", rede.getNamedEntities().stream().map(ne -> {
            Document doc = new Document();
            doc.put("text", ne.getCoveredText());
            doc.put("value", ne.getValue());
            doc.put("begin", ne.getBegin());
            doc.put("end", ne.getEnd());
            return doc;

        }).collect(Collectors.toList()));

        // Sentiment
        redeNLP.put("sentiment", rede.getSentiment().stream().map(s -> {
            Document doc = new Document();
            doc.put("sentiment", s.getSentiment());
            doc.put("pos", s.getPos());
            doc.put("neu", s.getNeu());
            doc.put("neg", s.getNeg());
            doc.put("begin", s.getBegin());
            doc.put("end", s.getEnd());
            return doc;

        }).collect(Collectors.toList()));

        mongoDB.update(redeNLP, "Reden");

        rede.setCasToNull(); // Arbeitsspeicher sparen :)
    }

    /**
     * Führt das NLP einer Liste von Reden durch und speichert zu jeder Rede das CAS-Objekt in der MongoDB ab
     *
     * @param reden die zu analysierende Reden
     */
    public void analyse(List<RedeMongoDBImpl> reden) throws Exception {

        this.nlpProgress = "0.00%";
        int failedUploads = 0;
        int curr = 1;

        long startTime = new java.util.Date().getTime();
        this.setStartTimeNLP(startTime);

        // Jede übergebene Rede analysieren und NLP-Ergebnisse in der MongoDB abspeichern
        for (RedeMongoDBImpl rede : reden) {

            // Abbruch, falls auf der Webseite der entsprechende Button gedrückt wurde
            if (!this.continueNLP()) {
                this.nlpProgress = "0.00%";

                this.setContinueNLP(true); // Damit die Analyse wieder gestartet werden kann
                this.composer.shutdown();  // Composer und damit die DockerDriver herunterfahren, um Arbeitsspeicher zu sparen
                return;
            }
            try {
                this.nlpProgress = String.format("%.2f", (double) curr * 100 / reden.size()).replace(",", ".") + "%";

                System.out.println("\n" + StringHelper.getCurrDateTimeFormatted() + " [NLP-Progress: " + this.getNLPProgress() + "] NLP of speech "
                        + rede.getID() + " (" + curr + "/" + reden.size() + ") and uploading CAS to MongoDB ...\n");
                this.analyse(rede);

                // Es gab eine Rede, bei der das CAS zu groß war:
            } catch (BsonMaximumSizeExceededException e) {
                this.nlpProgress = String.format("%.2f", (double) curr * 100 / reden.size()).replace(",", ".") + "%";


                System.out.println(StringHelper.getCurrDateTimeFormatted() + " [NLP-Progress: " + this.getNLPProgress() + "] Results of the NLP of the speech "
                        + rede.getID() + " (" + curr + "/" + reden.size() + ") could not be uploaded to MongoDB since payload document size is larger than maximum of 16793600.");
                failedUploads += 1;
            }
            curr += 1;

        }

        this.nlpProgress = "100%";
        this.composer.shutdown();

        // Dauer der Analyse messen
        long endTime = new java.util.Date().getTime();

        String duration = StringHelper.getPrettyDuration(startTime, endTime);

        System.out.println("\n" + StringHelper.getCurrDateTimeFormatted() + " Finished processing all speeches!\n" +
                "[INFORMATION] Amount of time your computer has been loaded with unnecessary tasks: " + duration);

        System.out.println("[INFORMATION] Number of failed uploads due to payload document size: " + failedUploads);
    }


    /**
     * Gibt das durchschnittliche Sentiment aller Reden dieser Fraktion im gegebenen Zeitraum zurück.
     * ACHTUNG: Umso länger der Zeitraum, desto länger braucht die Funktion, da wir ganz viele CAS-Einträge erstmal aus der MongoDB de-serialisieren müssen!
     *
     * @param fraktion die Fraktion, von der wir die durchschnittlichen Sentiment-Werte haben wollen (ganzer Name aus der DB)
     * @param von      der Beginn des Zeitraums im Format dd.MM.YYYY
     * @param bis      das Ende des Zeitraums im Format dd.MM.YYYY
     * @return das durchschnittliche Sentiment aller Reden dieser Fraktion (Keys: sentiment, pos, neu, neg)
     */
    public Map<String, Double> getAverageSentimentValues(String fraktion, String von, String bis)
            throws ParseException {

        Date filterDatumVon;
        Date filterDatumBis;

        try {
            filterDatumVon = StringHelper.toDate(von);

        } catch (ParseException e) {
            filterDatumVon = StringHelper.toDate("25.10.2021"); // Default: Datum der ersten erfassten Rede
        }

        try {
            filterDatumBis = StringHelper.toDate(bis);

        } catch (ParseException e) {
            filterDatumBis = new java.sql.Date(new java.util.Date().getTime()); // Default: Bis zum heutigen Datum
        }

        assert filterDatumVon != null;
        assert filterDatumBis != null;

        // Datenbankabfrage für die gesuchten IDs der Reden - abhängig von angegebener Fraktion und Zeitspanne
        List<Bson> query = Arrays.asList(
                Aggregates.unwind("$wahlperioden.WP20.fraktionen"),
                Aggregates.match(Filters.eq("wahlperioden.WP20.fraktionen", fraktion)),
                Aggregates.unwind("$reden"),
                Aggregates.lookup("Reden", "reden", "_id", "rede"),
                Aggregates.match(Filters.gte("rede.datum", filterDatumVon)),
                Aggregates.match(Filters.lte("rede.datum", filterDatumBis)),
                Aggregates.project(Projections.fields(
                        Projections.include("rede._id"),
                        Projections.include("rede.sentiment")
                )));

        MongoIterable<Document> resultDB = mongoDB.aggregate(query, "Abgeordnete");

        // Anzahl der Ergebnisse bestimmen
        int anzahlRedenDB = 0;

        for (Document ignored : resultDB) {
            anzahlRedenDB += 1;
        }

        if (anzahlRedenDB == 0) {
            return new HashMap<>();
        }

        // Sentiment-Werte aller zurückgegebenen Reden auslesen und den Durchschnitt ausrechnen
        double sumSentiment = 0;
        double sumPositive = 0;
        double sumNeutral = 0;
        double sumNegative = 0;

        for (Document doc : resultDB) {
            for (Document rede : doc.getList("rede", Document.class)) {
                try {
                    // Nur das erste Document des Sentiments interessiert uns, da dies das Sentiment der ganzen Rede widerspiegelt
                    Document overallSentimentOfSpeech = rede.getList("sentiment", Document.class).get(0);

                    sumSentiment += overallSentimentOfSpeech.getDouble("sentiment");
                    sumPositive += overallSentimentOfSpeech.getDouble("pos");
                    sumNeutral += overallSentimentOfSpeech.getDouble("neu");
                    sumNegative += overallSentimentOfSpeech.getDouble("neg");

                } catch (NullPointerException e) {
                    System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Das ist die eine Rede, " +
                            "bei der das CAS zu groß war, um sie in die MongoDB laden zu können. Wird in der Auswertung ignoriert.");
                    anzahlRedenDB -= 1;
                }
            }
        }

        // Durchschnittliche Sentiment-Werte in einer Map speichern:
        Map<String, Double> result = new HashMap<>();
        result.put("von", (double) filterDatumVon.getTime());
        result.put("bis", (double) filterDatumBis.getTime());
        result.put("anzahlReden", (double) anzahlRedenDB);
        result.put("avgSentiment", sumSentiment / anzahlRedenDB);
        result.put("avgPositive", sumPositive / (sumPositive + sumNegative + sumNeutral));
        result.put("avgNeutral", sumNeutral / (sumPositive + sumNegative + sumNeutral));
        result.put("avgNegative", sumNegative / (sumPositive + sumNegative + sumNeutral));

        return result;
    }

    /**
     * @return Gibt an, ob die Reden weiter analysiert werden sollen
     */
    public boolean continueNLP() {
        return this.continueNLP;
    }

    /**
     * @param continueNLP Gibt an, ob die Reden weiter analysiert werden sollen
     */
    public void setContinueNLP(boolean continueNLP) {
        this.continueNLP = continueNLP;
    }

    /**
     * @return den aktuellen Stand des NLP
     */
    public String getNLPProgress() {
        return this.nlpProgress;
    }

    /**
     * @param progress den aktuellen Stand des NLP
     */
    public void setNLPProgress(String progress) {
        this.nlpProgress = progress;
    }

    /**
     * @return der Zeitpunkt des Starts des NLPs der Reden
     */
    public long getStartTimeNLP() {
        return startTimeNLP;
    }

    /**
     * @param startTimeNLP der Zeitpunkt des Starts des NLPs der Reden
     */
    public void setStartTimeNLP(long startTimeNLP) {
        this.startTimeNLP = startTimeNLP;
    }
}
