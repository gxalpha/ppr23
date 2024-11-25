package org.texttechnologylab.project.Parliament_Browser_09_2.data;

import org.bson.Document;

/**
 * Custom interface to store named entities in the database.
 *
 * @author Stud
 */
public interface RedeElementNLPNamedEntity {
    String getValue();

    String getIdentifier();

    int getBegin();

    int getEnd();

    String getCoveredText();

    /**
     * @return Konvertiert das RedeElement zu einem Bson-Document
     */
    Document toDoc();
}
