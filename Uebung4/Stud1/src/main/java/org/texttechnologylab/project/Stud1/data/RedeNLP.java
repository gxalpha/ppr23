package org.texttechnologylab.project.Stud1.data;


import java.util.List;

/**
 * Interface für NLP-Daten einer Rede
 */
public interface RedeNLP {
    /**
     * @return Sentiment der Gesamtrede
     */
    double getSentiment();

    /**
     * @return Nomen der Rede
     */
    List<String> getNouns();

    /**
     * @return Named Entities der Rede
     */
    List<String> getNamedEntities();

    /**
     * @return Sätze der Rede
     */
    List<SentenceNLP> getSentences();
}
