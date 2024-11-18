package org.texttechnologylab.project.Stud1.data;

/**
 * Interface für einen NLP-analysierten Satz
 */
public interface SentenceNLP {
    /**
     * @return Text des Satzes
     */
    String getText();

    /**
     * @return Sentiment des Satzes
     */
    double getSentiment();
}
