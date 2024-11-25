package org.texttechnologylab.project.Parliament_Browser_09_2.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElementNLPNamedEntity;

/**
 * Klasse die eine Named Entity aus einer NLP-Analyse darstellt.
 *
 * @author Stud
 */
public class RedeElementNLPNamedEntityImpl implements RedeElementNLPNamedEntity {
    private final String value;
    private final String identifier;
    private final int begin;
    private final int end;
    private final String coveredText;

    public RedeElementNLPNamedEntityImpl(String value, String identifier, int begin, int end, String coveredText) {
        this.value = value;
        this.identifier = identifier;
        this.begin = begin;
        this.end = end;
        this.coveredText = coveredText;
    }

    public RedeElementNLPNamedEntityImpl(Document document) {
        this.value = document.getString("value");
        this.identifier = document.getString("identifier");
        this.begin = document.getInteger("begin");
        this.end = document.getInteger("end");
        this.coveredText = document.getString("coveredText");
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public int getBegin() {
        return begin;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public String getCoveredText() {
        return coveredText;
    }

    /**
     * @return NamedEntity als Bson-Document
     * @author Stud, Stud
     */
    @Override
    public Document toDoc() {
        Document document = new Document();
        document.put("value", getValue());
        document.put("identifier", getIdentifier());
        document.put("begin", getBegin());
        document.put("end", getEnd());
        document.put("coveredText", getCoveredText());
        return document;
    }
}
