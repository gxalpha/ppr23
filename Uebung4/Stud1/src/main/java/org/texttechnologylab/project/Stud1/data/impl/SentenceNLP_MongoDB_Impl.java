package org.texttechnologylab.project.Stud1.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Stud1.data.SentenceNLP;


/**
 * Implementierung von SentenceNLP
 */
public class SentenceNLP_MongoDB_Impl implements SentenceNLP {

    private final String text;
    private final double sentiment;

    /**
     * Konstruktor über den Inhalt
     *
     * @param text      Text
     * @param sentiment Sentiment
     */
    public SentenceNLP_MongoDB_Impl(String text, double sentiment) {
        this.text = text;
        this.sentiment = sentiment;
    }

    /**
     * @param document Konstruktor über ein Dokument
     */
    public SentenceNLP_MongoDB_Impl(Document document) {
        this.text = document.getString(Keys.TEXT);
        this.sentiment = document.getDouble(Keys.SENTIMENT);
    }

    /**
     * @return Text des Satzes
     */
    @Override
    public String getText() {
        return text;
    }

    /**
     * @return Sentiment des Satzes
     */
    @Override
    public double getSentiment() {
        return sentiment;
    }

    /**
     * Hilfsklasse zum Vermeiden von Typos.
     */
    public static class Keys {
        public static final String TEXT = "text";
        public static final String SENTIMENT = "sentiment";
    }
}
