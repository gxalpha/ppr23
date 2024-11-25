package org.texttechnologylab.project.Parliament_Browser_09_2.database;

import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;

import java.util.Set;

/**
 * Interface für die Übertragung des Abgeordneten in die Datenbank
 *
 * @author Stud
 */
public interface AbgeordneterDB {

    /**
     * Umwandlung des Abgeordneten in ein Document
     *
     * @param abgeordneter Abgeordneter, der in Datenbank eingefügt werden soll
     * @return Document, dass in Datenbank eingefügt werden kann
     * @author Stud
     */
    Document abgeordneterToDocument(Abgeordneter abgeordneter);

    /**
     * Einfügen von Abgeordneten in die Datenbank
     *
     * @param abgeordnete Abgeordnete, die in Datenbank eingefügt werden sollen
     * @author Stud
     * @author Stud (Änderungen)
     */
    void insertAbgeordnetenDB(Set<Abgeordneter> abgeordnete);

    /**
     * Updatet einen Abgeordneten in der Datenbank
     *
     * @param abgeordneter Der Abgeordnete
     * @author Stud
     */
    void updateAbgeordneterDB(Abgeordneter abgeordneter);

}
