package org.texttechnologylab.project.Stud2.database;

/**
 * Interface für Objekte, welche die Zugangsdaten zu einer Datenbank halten
 *
 * @author Stud2
 */
public interface MongoDBProperties {
    /**
     * @return den remote_host
     */
    String getRemoteHost();

    /**
     * @return die remote_database
     */
    String getRemoteDatabase();

    /**
     * @return den remote_user
     */
    String getRemoteUser();

    /**
     * @return das remote_password
     */
    String getRemotePassword();

    /**
     * @return den remote_port
     */
    String getRemotePort();

    /**
     * @return die remote_collection
     */
    String getRemoteCollection();
}
