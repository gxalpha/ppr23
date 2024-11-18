package org.texttechnologylab.project.Stud1.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Custom Properity-Klasse für unsere Datenbank
 */
public class MongoProperties extends Properties {
    /**
     * Konstruktor
     *
     * @param dbConfig InputStream mit den Daten
     * @throws IOException Bei Fehlern.
     */
    public MongoProperties(InputStream dbConfig) throws IOException {
        super.load(dbConfig);
    }

    /**
     * @return Host
     */
    public String getRemoteHost() {
        return super.getProperty("remote_host");
    }

    /**
     * @return Datenbank
     */
    public String getRemoteDatabase() {
        return super.getProperty("remote_database");
    }

    /**
     * @return Nutzer
     */
    public String getRemoteUser() {
        return super.getProperty("remote_user");
    }

    /**
     * @return Passwort
     */
    public String getRemotePassword() {
        return super.getProperty("remote_password");
    }

    /**
     * @return Port
     */
    public int getRemotePort() {
        return Integer.parseInt(super.getProperty("remote_port"));
    }

    /**
     * @return Default-Collection
     */
    public String getRemoteCollection() {
        return super.getProperty("remote_collection");
    }

    /**
     * DO NOT USE!
     *
     * @param key irrelevant
     * @return nothing
     */
    @Override
    public String getProperty(String key) {
        throw new RuntimeException("Use the proper methods!");
    }
}
