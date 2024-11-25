package org.texttechnologylab.project.Parliament_Browser_09_2.database;

import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Rede;

/**
 * Interface, das Reden in Datenbank schreibt
 *
 * @author Stud
 * @author Stud (Änderungen)
 */
public interface RedeDB {

    /**
     * @param rede Rede, die in Datenbank eingefügt werden soll
     * @return Document der Rede
     * @author Stud
     */
    Document redeToDocument(Rede rede);

    /**
     * Einfügen von Reden in die Datenbank
     *
     * @param rede Rede, die in die Datenbank eingefügt werden sollen
     * @author Stud
     */
    void insertRedeDB(Rede rede, Abgeordneter redner);

    /**
     * Updatet eine Rede in der Datenbank
     *
     * @param rede
     * @author Stud
     */
    void updateRedeDB(Rede rede);
}
