package org.texttechnologylab.project.Stud1.nlp;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.type.GerVaderSentiment;
import org.hucompute.textimager.uima.type.Sentiment;
import org.texttechnologylab.project.Stud1.data.Rede;
import org.texttechnologylab.project.Stud1.data.RedeNLP;
import org.texttechnologylab.project.Stud1.data.SentenceNLP;
import org.texttechnologylab.project.Stud1.data.impl.RedeNLP_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.Rede_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.SentenceNLP_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.database.MongoDBConnectionHandler;
import org.texttechnologylab.project.Stud1.util.Static;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Background worker for the NLP analysis.
 * Only one should ever exist, which is why it's effectively a static object.
 */
public class NLPAnalyzer {
    private static Thread runner;
    private static boolean shouldContinue;
    private static boolean isReady;
    private static int totalSpeeches;
    private static int processedSpeeches;

    static {
        runner = null;
        shouldContinue = false;
        isReady = false;
        totalSpeeches = 0;
        processedSpeeches = 0;
    }

    /**
     * Private Constructor, don't use.
     */
    private NLPAnalyzer() {
        throw new RuntimeException("Nah.");
    }

    /**
     * Initialisiert den Analyzer, damit Daten wie Fortschritt richtig getrackt werden.
     *
     * @param handler MongoDBConnectionHandler um den Fortschritt aus der Datenbank holen zu können
     */
    public static void initProgress(MongoDBConnectionHandler handler) {
        processedSpeeches = (int) handler.getDatabase().getCollection(Rede_MongoDB_Impl.JCasKeys.COLLECTION_NAME).countDocuments();
        totalSpeeches = (int) handler.getDatabase().getCollection(Rede_MongoDB_Impl.Keys.COLLECTION_NAME).countDocuments();
        isReady = true;
    }

    /**
     * Startet die Analyse
     *
     * @param handler MongoDBConnectionHandler um auf die Datenbank zuzugreifen
     */
    public static void start(MongoDBConnectionHandler handler) {
        if (runner != null) {
            return;
        }
        if (!Static.ENABLE_NLP) {
            System.err.println("NLP is disabled, not starting NLP despite request (but setting running to true).");
            shouldContinue = true;
            return;
        }

        runner = new Thread(() -> {
            while (shouldContinue) {
                handler.getDatabase().getCollection(Rede_MongoDB_Impl.Keys.COLLECTION_NAME).aggregate(List.of(
                        Aggregates.lookup(Rede_MongoDB_Impl.JCasKeys.COLLECTION_NAME, Rede_MongoDB_Impl.Keys.ID, Rede_MongoDB_Impl.JCasKeys.ID, "other_tmp"),
                        Aggregates.match(Filters.size("other_tmp", 0)),
                        // Only take small numbers so that it doesn't take ages for the if statement to be
                        // evaluated to false for every speech.
                        Aggregates.limit(10)
                )).map(Rede_MongoDB_Impl::new).forEach((Consumer<? super Rede>) rede -> {
                    if (!shouldContinue) {
                        return;
                    }

                    try {
                        String id = rede.getID();
                        System.out.println(id + ": Analyzing...");
                        JCas jCas = DUUIHelper.analyze(rede);

                        /* Objektorientierte Verarbeitung und Upload via RedeNLP- und SentenceNLP-Interfaces */
                        List<SentenceNLP> nlpSentences = new ArrayList<>();
                        List<String> namedEntities = new ArrayList<>();
                        List<String> nouns = new ArrayList<>();
                        JCasUtil.select(jCas, Token.class).forEach(token -> {
                            // Token erfolgreich extrahiert, wie in 2c) gefordert.
                            // Er wird nie wieder verwendet, braucht also nicht weiter verarbeitet zu werden.
                        });
                        JCasUtil.select(jCas, Sentence.class).forEach(sentence -> {
                            JCasUtil.selectCovered(Sentiment.class, sentence).forEach(sentiment -> {
                                nlpSentences.add(new SentenceNLP_MongoDB_Impl(sentence.getCoveredText(), sentiment.getSentiment()));
                            });
                        });
                        JCasUtil.select(jCas, POS.class).forEach(pos -> {
                            if (pos.getPosValue().equals("NN")) {
                                nouns.add(pos.getCoveredText());
                            }
                        });
                        JCasUtil.select(jCas, Dependency.class).forEach(dependency -> {
                            // Dependency erfolgreich extrahiert, wie in 2c) gefordert.
                            // Er wird nie wieder verwendet, braucht also nicht weiter verarbeitet zu werden.
                        });
                        JCasUtil.select(jCas, NamedEntity.class).forEach(namedEntity -> namedEntities.add(namedEntity.getCoveredText()));

                        // Erstes ist immer das Gesamtsentiment, danach kommen die Sätze die wir schon von oben haben
                        double sentiment = JCasUtil.select(jCas, GerVaderSentiment.class).stream().findFirst().get().getSentiment();

                        RedeNLP nlpDaten = new RedeNLP_MongoDB_Impl(sentiment, nouns, namedEntities, nlpSentences);

                        System.out.println(id + ": Finished analyzing. Uploading...");
                        handler.uploadRedeJCas(rede.getID(), jCas);
                        handler.updateRedeWithNLPData(rede.getID(), nlpDaten);
                        processedSpeeches++;
                        System.out.println(id + ": Finished uploading.");
                    } catch (Exception e) {
                        System.err.println("NOPE WE'VE HAD A PROBLEM:");
                        e.printStackTrace();
                    }
                });
            }

            // No idea if this works. In theory it should.
            if (shouldContinue) {
                new Thread(() -> {
                    try {
                        stop();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        });
        shouldContinue = true;
        runner.start();
    }

    /**
     * Stoppt die NLP-Analyse.
     *
     * @throws InterruptedException If bad things happen
     */
    public static void stop() throws InterruptedException {
        if (!Static.ENABLE_NLP) {
            System.err.println("NLP is disabled, only setting running to false.");
            shouldContinue = false;
            return;
        }

        if (runner != null) {
            System.out.println("Stopping NLP, this may take while...");
            shouldContinue = false;
            runner.join();
            runner = null;
            System.out.println("NLP stopped.");
        }
    }

    /**
     * Returns the progress of the total analysis.
     *
     * @return Progress as 0-1.
     */
    public static double getProgress() {
        return (double) processedSpeeches / totalSpeeches;
    }

    /**
     * Returns whether the analyzer is ready to run.
     *
     * @return True if ready, false otherwise.
     */
    public static boolean isReady() {
        return isReady;
    }

    /**
     * Check if the analyzer is running in any way.
     *
     * @return True if the analyzer is running, false otherwise.
     */
    public static boolean isRunning() {
        return shouldContinue || runner != null;
    }
}
