package org.texttechnologylab.project.Parliament_Browser_09_2.database;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.hucompute.textimager.uima.type.GerVaderSentiment;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

/**
 * Interface, to get NLP-Data to MongoDB
 *
 * @author Stud
 */
public interface NlpDB {

    /**
     * @param tokenList Ergebnis der NLP-Analyse
     * @return Document mit den Inhalten der NLP-Analyse
     * @author Stud
     */
    List<Document> createTokensDocument(List<Token> tokenList);

    /**
     * @param sentenceList Ergebnis der NLP-Analyse
     * @return Document mit den Inhalten der NLP-Analyse
     * @author Stud
     */
    List<Document> createSentencesDocument(List<Sentence> sentenceList);

    /**
     * @param posList Ergebnis der NLP-Analyse
     * @return Document mit den Inhalten der NLP-Analyse
     * @author Stud
     */
    List<Document> createPOSDocument(List<POS> posList);

    /**
     * @param dependencyList Ergebnis der NLP-Analyse
     * @return Document mit den Inhalten der NLP-Analyse
     * @author Stud
     */
    List<Document> createDependencyDocument(List<Dependency> dependencyList);

    /**
     * @param gerVaderSentimentList Ergebnis der NLP-Analyse
     * @return Document mit den Inhalten der NLP-Analyse
     * @author Stud
     */
    List<Document> createSentimentDocument(List<GerVaderSentiment> gerVaderSentimentList);

    /**
     * @param jCas JCas, das bei Analyse erstellt werden muss
     * @return String, der in Datenbank geschrieben werden kann
     * @author Stud
     */
    String serialiseJCas(JCas jCas) throws SAXException;

    /**
     * @param jCasString String, der in der Datenbank stand
     * @return JCas, das aus String resultiert
     * @author Stud
     */
    JCas deserialiseJCas(String jCasString) throws UIMAException, IOException, SAXException;

}
