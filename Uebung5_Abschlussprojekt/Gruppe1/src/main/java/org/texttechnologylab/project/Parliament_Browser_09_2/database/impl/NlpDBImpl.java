package org.texttechnologylab.project.Parliament_Browser_09_2.database.impl;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.hucompute.textimager.uima.type.GerVaderSentiment;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.NlpDB;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasse, die das Schreiben der NLP-Daten in die DB erleichtert
 *
 * @author Stud
 */
public class NlpDBImpl implements NlpDB {
    /**
     * @param tokenList Ergebnis der NLP-Analyse
     * @return Ergebnisse als Document
     * @author Stud
     */
    @Override
    public List<Document> createTokensDocument(List<Token> tokenList) {
        List<Document> documentsToken = new ArrayList<>();

        // alle Token der Liste durchlaufen
        for (Token token : tokenList) {
            Document tokenDocument = new Document();

            // Inhalte der Analyse ausgelesen und in Document geschrieben
            tokenDocument.put("lemma", token.getLemmaValue());
            tokenDocument.put("morph", token.getMorph().getValue());
            tokenDocument.put("begin", token.getBegin());
            tokenDocument.put("end", token.getEnd());
            tokenDocument.put("coveredText", token.getCoveredText());

            // Document der Liste hinzugefügt
            documentsToken.add(tokenDocument);
        }

        // Liste, die in Datenbank geschrieben wird
        return documentsToken;
    }

    /**
     * @param sentenceList Ergebnis der NLP-Analyse
     * @return Ergebnisse als Document
     * @author Stud
     */
    @Override
    public List<Document> createSentencesDocument(List<Sentence> sentenceList) {
        List<Document> documentsSentences = new ArrayList<>();

        // alle Sentences der Liste durchlaufen
        for (Sentence sentence : sentenceList) {
            Document sentenceDocument = new Document();

            // Inhalte der Analyse ausgelesen und in Document geschrieben
            sentenceDocument.put("begin", sentence.getBegin());
            sentenceDocument.put("end", sentence.getEnd());
            sentenceDocument.put("coveredText", sentence.getCoveredText());

            // Document der Liste hinzugefügt
            documentsSentences.add(sentenceDocument);
        }

        // Liste, die in Datenbank geschrieben wird
        return documentsSentences;
    }

    /**
     * @param posList Ergebnis der NLP-Analyse
     * @return Ergebnisse als Document
     * @author Stud
     */
    @Override
    public List<Document> createPOSDocument(List<POS> posList) {
        List<Document> documentsPOS = new ArrayList<>();

        // alle POS der Liste durchlaufen
        for (POS pos : posList) {
            Document posDocument = new Document();

            // Inhalte der Analyse ausgelesen und in Document geschrieben
            posDocument.put("posValue", pos.getPosValue());
            posDocument.put("coarseValue", pos.getCoarseValue());
            posDocument.put("begin", pos.getBegin());
            posDocument.put("end", pos.getEnd());
            posDocument.put("coveredText", pos.getCoveredText());

            // Document der Liste hinzugefügt
            documentsPOS.add(posDocument);
        }

        // Liste, die in Datenbank geschrieben wird
        return documentsPOS;
    }

    /**
     * @param dependencyList Ergebnis der NLP-Analyse
     * @return Ergebnisse als Document
     * @author Stud
     */
    @Override
    public List<Document> createDependencyDocument(List<Dependency> dependencyList) {
        List<Document> documentsDependency = new ArrayList<>();

        // alle Dependencies der Liste durchlaufen
        for (de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency dependency : dependencyList) {
            Document dependencyDocument = new Document();

            // Inhalte der Analyse ausgelesen und in Document geschrieben
            dependencyDocument.put("governor", dependency.getGovernor().getCoveredText());
            dependencyDocument.put("dependent", dependency.getDependent().getCoveredText());
            dependencyDocument.put("dependencyType", dependency.getDependencyType());
            dependencyDocument.put("flavor", dependency.getFlavor());
            dependencyDocument.put("begin", dependency.getBegin());
            dependencyDocument.put("end", dependency.getEnd());
            dependencyDocument.put("coveredText", dependency.getCoveredText());

            // Document der Liste hinzugefügt
            documentsDependency.add(dependencyDocument);
        }

        // Liste, die in Datenbank geschrieben wird
        return documentsDependency;
    }

    /**
     * @param gerVaderSentimentList Ergebnis der NLP-Analyse
     * @return Ergebnisse als Document
     * @author Stud
     */
    @Override
    public List<Document> createSentimentDocument(List<GerVaderSentiment> gerVaderSentimentList) {
        List<Document> documentsGerVaderSentiment = new ArrayList<>();

        // alle Sentiments der Liste durchlaufen
        for (GerVaderSentiment gerVaderSentiment : gerVaderSentimentList) {
            Document gerVaderSentimentDocument = new Document();

            // Inhalte der Analyse ausgelesen und in Document geschrieben
            gerVaderSentimentDocument.put("pos", gerVaderSentiment.getPos());
            gerVaderSentimentDocument.put("neg", gerVaderSentiment.getNeg());
            gerVaderSentimentDocument.put("neu", gerVaderSentiment.getNeu());
            gerVaderSentimentDocument.put("sentiment", gerVaderSentiment.getSentiment());
            gerVaderSentimentDocument.put("subjectivity", gerVaderSentiment.getSubjectivity());
            gerVaderSentimentDocument.put("begin", gerVaderSentiment.getBegin());
            gerVaderSentimentDocument.put("end", gerVaderSentiment.getEnd());

            // Document der Liste hinzugefügt
            documentsGerVaderSentiment.add(gerVaderSentimentDocument);
        }

        // Liste, die in Datenbank geschrieben wird
        return documentsGerVaderSentiment;
    }

    /**
     * @param jCas JCas, das bei Analyse erstellt werden muss
     * @return JCas-Inhalte als String
     * @author Stud
     */
    @Override
    public String serialiseJCas(JCas jCas) throws SAXException {
        // Verwendung des serialisierers
        XmiCasSerializer serializer = new XmiCasSerializer(jCas.getTypeSystem());

        // Ouputstream auf den Ergebnis geschrieben werden soll
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // serialisieren des JCas
        serializer.serialize(jCas.getCas(), outputStream);

        // String aus Outputstream erzeugt
        String serializedJCas = outputStream.toString(StandardCharsets.UTF_8);

        return serializedJCas;
    }

    /**
     * @param jCasString String, der in der Datenbank stand
     * @return String als JCas
     * @author Stud
     */
    @Override
    public JCas deserialiseJCas(String jCasString) throws UIMAException, IOException, SAXException {
        // String zu InputStream umwandeln
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jCasString.getBytes(StandardCharsets.UTF_8));
        // JCas erzeugen, welches leer ist
        JCas jCas = JCasFactory.createJCas();
        // JCas mit Inhalten aus String füllen
        XmiCasDeserializer.deserialize(inputStream, jCas.getCas());

        return jCas;
    }
}
