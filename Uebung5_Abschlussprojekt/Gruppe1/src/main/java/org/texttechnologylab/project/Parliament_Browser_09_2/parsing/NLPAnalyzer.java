package org.texttechnologylab.project.Parliament_Browser_09_2.parsing;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.bson.BsonMaximumSizeExceededException;
import org.bson.Document;
import org.hucompute.textimager.uima.type.GerVaderSentiment;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.texttechnologylab.DockerUnifiedUIMAInterface.DUUIComposer;
import org.texttechnologylab.DockerUnifiedUIMAInterface.driver.DUUIDockerDriver;
import org.texttechnologylab.DockerUnifiedUIMAInterface.lua.LuaConsts;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Rede;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeCas;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElement;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElementCas;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.RedeCasImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.RedeElementCasImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.RedeElementNLPImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.MongoDBHandlerImpl;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to analyze speeches using DUUI.
 * Since we don't want to have multiple of the same Docker image running, so we make sure you CANNOT create multiple
 * objects of this class.
 * This behaves like the Kotlin "Object" class type by having everything as static methods.
 *
 * @author Stud
 * @author Stud (Bearbeitet)
 */
public class NLPAnalyzer {

    private static final DUUIComposer duuiComposerSegment;
    private static final DUUIComposer duuiComposerFullSpeech;
    private static final int NUMBER_OF_COMPOSERS = 2;
    private static int readyCount;

    static {
        readyCount = 0;

        if (true /* Static.ENABLE_NLP */ /* This will be a problem for another time */) {
            try {
                duuiComposerSegment = new DUUIComposer()
                        .withSkipVerification(true)
                        .withLuaContext(LuaConsts.getJSON())
                        .withWorkers(1)
                        .addDriver(new DUUIDockerDriver())
                        .add(new DUUIDockerDriver.Component("docker.texttechnologylab.org/textimager-duui-spacy-single-de_core_news_sm:0.1.4")
                                .withScale(1)
                                .build())
                        .add(new DUUIDockerDriver.Component("docker.texttechnologylab.org/gervader_duui:1.0.2")
                                .withParameter("selection", "text")
                                .withScale(1)
                                .build());

                duuiComposerFullSpeech = new DUUIComposer()
                        .withSkipVerification(true)
                        .withLuaContext(LuaConsts.getJSON())
                        .withWorkers(1)
                        .addDriver(new DUUIDockerDriver())
                        .add(new DUUIDockerDriver.Component("docker.texttechnologylab.org/parlbert-topic-german:latest")
                                .withScale(1)
                                .build())
                        /* We don't need any SpaCy-results directly but GerVader requires it */
                        .add(new DUUIDockerDriver.Component("docker.texttechnologylab.org/textimager-duui-spacy-single-de_core_news_sm:0.1.4")
                                .withScale(1)
                                .build())
                        .add(new DUUIDockerDriver.Component("docker.texttechnologylab.org/gervader_duui:1.0.2")
                                .withParameter("selection", "text")
                                .withScale(1)
                                .build());

                new Thread(() -> {
                    try {
                        JCas jCas = JCasFactory.createJCas();
                        jCas.setDocumentText("Dies ist Text, um die Container zu starten.");
                        duuiComposerSegment.run(jCas);
                        readyCount++;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Testrun der Sentimentanalyse erfolgreich");
                }).start();
                new Thread(() -> {
                    try {
                        JCas jCas = JCasFactory.createJCas();
                        jCas.setDocumentText("Was erlauben Strunz?!");
                        duuiComposerFullSpeech.run(jCas);
                        readyCount++;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Testrun der Topicanalyse erfolgreich");
                }).start();

            } catch (URISyntaxException | IOException | UIMAException | SAXException | CompressorException e) {
                throw new RuntimeException(e);
            }
        } else {
            duuiComposerSegment = null;
            duuiComposerFullSpeech = null;
            readyCount = NUMBER_OF_COMPOSERS;
        }
    }

    /**
     * Unusable constructor.
     * You must construct additional pylons!
     *
     * @author Stud
     */
    private NLPAnalyzer() {
        throw new RuntimeException("No.");
    }

    /**
     * NLP-analysiert ein RedeElement und setzt die NLP-Daten.
     *
     * @param element Zu analysierendes Element
     * @author Stud
     * @author Stud (Bearbeitet)
     */
    private static JCas analyzeElement(RedeElement element) {
        JCas jCas;
        try {
            jCas = element.toCAS();
            duuiComposerSegment.run(jCas);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        /* I didn't know this was a thing. Nice. */
        var nlpInfo = new Object() {
            double sentiment = 0;
            double positive = 0;
            double negative = 0;
            double neutral = 0;
        };
        JCasUtil.select(jCas, GerVaderSentiment.class).stream().findFirst().ifPresent(sentiment -> {
            nlpInfo.sentiment = sentiment.getSentiment();
            nlpInfo.positive = sentiment.getPos();
            nlpInfo.negative = sentiment.getNeg();
            nlpInfo.neutral = sentiment.getNeu();
        });
        element.setNLP(new RedeElementNLPImpl(
                nlpInfo.sentiment,
                new ArrayList<>(JCasUtil.select(jCas, POS.class)),
                new ArrayList<>(JCasUtil.select(jCas, NamedEntity.class)),
                nlpInfo.positive,
                nlpInfo.neutral,
                nlpInfo.negative));

        return jCas;
    }

    /**
     * Analysiert eine Rede nach ihrem Topic und setzt dieses.
     *
     * @param rede Rede, deren Topic gefunden werden soll
     * @author Stud
     * @author Stud (Bearbeitet)
     */
    private static JCas analyzeFullText(Rede rede) {
        JCas jCas;
        try {
            jCas = rede.toCAS();
            duuiComposerFullSpeech.run(jCas);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        rede.setTopic(JCasUtil.select(jCas, CategoryCoveredTagged.class)
                .stream()
                .findFirst()
                .map(CategoryCoveredTagged::getValue)
                .orElse(null));
        JCasUtil.select(jCas, GerVaderSentiment.class).stream().findFirst().ifPresent(sentiment -> {
            rede.setSentiment(sentiment.getSentiment());
            rede.setPositiv(sentiment.getPos());
            rede.setNegativ(sentiment.getNeg());
            rede.setNeutral(sentiment.getNeu());
        });

        return jCas;
    }

    /**
     * Analysiert eine Rede und fügt ihr die Analyseergebnisse hinzu. Läd RedeCas Objekte in Datenbank.
     *
     * @param rede Die Rede
     * @author Stud, Stud
     */
    public static synchronized void analyze(Rede rede) throws UIMAException, IOException, SAXException {
        /* Make sure we're good to go first */
        while (!isReady()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // NLP analysiere Rede und erstelle Cas-Objekte
        JCas casFull = analyzeFullText(rede);

        List<RedeElementCas> redeElementCasList = new ArrayList<>();

        List<RedeElement> redeElementList = rede.getRedeElemente();
        for (RedeElement element : redeElementList) {

            NLPAnalyzer.analyzeElement(element);

            RedeElementCas redeElementCas = new RedeElementCasImpl(element.toCAS());
            redeElementCasList.add(redeElementCas);

        }

        RedeCas redeCas = new RedeCasImpl(rede.getID(), casFull, redeElementCasList);

        // Lade Cas Objekte in Datenbank

        MongoDBHandler mongoDBHandler = new MongoDBHandlerImpl("Project_09_02.txt");
        MongoCollection<Document> collection = mongoDBHandler.getMongoDatabase().getCollection("reden_Cas");
        ReplaceOptions options = new ReplaceOptions();
        options.upsert(true);
        try {
            collection.replaceOne(Filters.eq("_id", redeCas.getRedeID()), redeCas.toDoc(), options);
        } catch (BsonMaximumSizeExceededException ignored) {
            // Ignore documents that are too large. There are only a few of them and they're not that important.
        }

    }

    /**
     * @return Ob die NLP-Analyse bereit ist
     */
    public static boolean isReady() {
        return readyCount == NUMBER_OF_COMPOSERS;
    }
}
