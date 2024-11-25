package org.texttechnologylab.project.Parliament_Browser_09_2.data.impl;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElementNLP;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElementNLPNamedEntity;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.NlpDB;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.NlpDBImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Ein Rede-Element mit den NLP-Ergebnissen
 *
 * @author Stud
 * @author Stud (geändert)
 * @author Stud (geändert)
 */
public class RedeElementNLPImpl implements RedeElementNLP {
    private final double sentiment;
    private final double positiv;
    private final double negativ;
    private final double neutral;
    private final List<POS> pos;
    private final List<RedeElementNLPNamedEntity> namedEntities;

    /**
     * Konstruktor
     *
     * @param sentiment     Sentiment des gesamten Rede-Elements
     * @param pos           die POS
     * @param namedEntities die Named Entities
     */
    public RedeElementNLPImpl(double sentiment, List<POS> pos, List<NamedEntity> namedEntities, double positiv, double neutral, double negativ) {
        this.sentiment = sentiment;
        this.pos = pos;
        this.namedEntities = namedEntities.stream().map(namedEntity -> new RedeElementNLPNamedEntityImpl(namedEntity.getValue(), namedEntity.getIdentifier(), namedEntity.getBegin(), namedEntity.getEnd(), namedEntity.getCoveredText())).collect(Collectors.toList());
        this.positiv = positiv;
        this.negativ = negativ;
        this.neutral = neutral;
    }

    /**
     * Konstruktor von der Datenbank.
     * Ggf. noch unvollständig.
     *
     * @param document Document aus der MongoDB
     * @author Stud
     */
    public RedeElementNLPImpl(Document document) {
        this.sentiment = document.getDouble("sentiment");
        this.positiv = document.getDouble("positiv");
        this.negativ = document.getDouble("negativ");
        this.neutral = document.getDouble("neutral");
        // We don't need this currently. Otherwise we'll make another custom class.
        this.pos = List.of();
        this.namedEntities = document.getList("namedEntities", Document.class).stream().map(RedeElementNLPNamedEntityImpl::new).collect(Collectors.toList());
    }

    /**
     * @return Sentiment des Rede-Elements
     */
    @Override
    public double getSentiment() {
        return this.sentiment;
    }

    /**
     * @return die POS (Parts of speech)
     */
    @Override
    public List<POS> getPOS() {
        return this.pos;
    }

    /**
     * @return NamedEntities des Rede-Elements
     */
    @Override
    public List<RedeElementNLPNamedEntity> getNamedEntities() {
        return this.namedEntities;
    }

    /**
     * @return Objekt in ihrer MongoDB-Repräsentation
     */
    @Override
    public Document toDoc() {
        NlpDB nlpDB = new NlpDBImpl();

        Document document = new Document();
        document.put("sentiment", this.sentiment);
        document.put("positiv", this.positiv);
        document.put("negativ", this.negativ);
        document.put("neutral", this.neutral);

        // POS in Document einfügen
        document.put("pos", nlpDB.createPOSDocument(this.pos));

        // namedEntities in Document einfügen
        document.put("namedEntities", this.namedEntities.stream().map(RedeElementNLPNamedEntity::toDoc).collect(Collectors.toList()));

        return document;
    }
}
