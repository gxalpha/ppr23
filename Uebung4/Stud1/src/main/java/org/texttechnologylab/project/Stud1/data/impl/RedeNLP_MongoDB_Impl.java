package org.texttechnologylab.project.Stud1.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Stud1.data.RedeNLP;
import org.texttechnologylab.project.Stud1.data.SentenceNLP;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementierung von RedeNLP
 */
public class RedeNLP_MongoDB_Impl implements RedeNLP {
    private final double sentiment;
    private final List<String> nouns;
    private final List<String> namedEntities;
    private final List<SentenceNLP> sentences;

    /**
     * Konstruktor über die Daten
     *
     * @param sentiment     Sentiment der Rede
     * @param nouns         Nomen
     * @param namedEntities Named Entities
     * @param sentences     Sentences
     */
    public RedeNLP_MongoDB_Impl(double sentiment, List<String> nouns, List<String> namedEntities, List<SentenceNLP> sentences) {
        this.sentiment = sentiment;
        this.nouns = nouns;
        this.namedEntities = namedEntities;
        this.sentences = sentences;
    }

    /**
     * Konstruktor für die Rede mit einem Dokument
     *
     * @param document Dokument
     */
    public RedeNLP_MongoDB_Impl(Document document) {
        this.sentiment = document.getDouble(Keys.SENTIMENT);
        this.nouns = document.getList(Keys.NOUNS, String.class);
        this.namedEntities = document.getList(Keys.NAMED_ENTITIES, String.class);
        this.sentences = document.getList(Keys.SENTENCES, Document.class).stream().map(SentenceNLP_MongoDB_Impl::new).collect(Collectors.toList());
    }

    /**
     * @return Sentiment der Gesamtrede
     */
    @Override
    public double getSentiment() {
        return sentiment;
    }

    /**
     * @return Nomen der Rede
     */
    @Override
    public List<String> getNouns() {
        return new ArrayList<>(nouns);
    }

    /**
     * @return Named Entities der Rede
     */
    @Override
    public List<String> getNamedEntities() {
        return new ArrayList<>(namedEntities);
    }

    /**
     * @return Sätze der Rede
     */
    @Override
    public List<SentenceNLP> getSentences() {
        return new ArrayList<>(sentences);
    }

    /**
     * Hilfsklasse zum Vermeiden von Typos.
     */
    public static class Keys {
        public static final String SENTIMENT = "sentiment";
        public static final String NAMED_ENTITIES = "named_entities";
        public static final String NOUNS = "nouns";
        public static final String SENTENCES = "sentences";
    }
}
