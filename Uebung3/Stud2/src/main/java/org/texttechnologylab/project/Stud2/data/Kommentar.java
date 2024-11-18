package org.texttechnologylab.project.Stud2.data;

import org.bson.Document;

/**
 * Ein Interface für einen Kommentar
 *
 * @author Stud2
 */
public interface Kommentar extends BundestagObject {
    /**
     * @return die ID der Rede, in der kommentiert wurde
     */
    int getRedeID();

    /**
     * @return die ID des Abgeordneten, der den Kommentar geäußert hat
     */
    int getAbgeordneterID();

    /**
     * @return der Inhalt des Kommentars
     */
    String getText();

    /**
     * @return der Kommentar als Dokument
     */
    Document toDoc();
}
