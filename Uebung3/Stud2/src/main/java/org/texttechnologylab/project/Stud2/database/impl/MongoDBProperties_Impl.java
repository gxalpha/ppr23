package org.texttechnologylab.project.Stud2.database.impl;

import org.texttechnologylab.project.Stud2.database.MongoDBProperties;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Klasse für Objekte, die die Zugangsdaten für die MongoDB enthalten
 *
 * @author Stud2
 */
public class MongoDBProperties_Impl extends Properties implements MongoDBProperties {
    private final String remote_host;
    private final String remote_database;
    private final String remote_user;
    private final String remote_password;
    private final String remote_port;
    private final String remote_collection;

    /**
     * Konstruktor für ein Objekt der Klasse MyProperties, die die Datenbankzugangsdaten enthält
     *
     * @param path der Pfad zu MongoDB.properties
     */
    public MongoDBProperties_Impl(String path) throws IOException {
        super();

        // Konfigurationsdatei MongoDB.properties einlesen
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(path));
        this.load(stream);
        stream.close();

        this.remote_host = this.getProperty("remote_host");
        this.remote_database = this.getProperty("remote_database");
        this.remote_user = this.getProperty("remote_user");
        this.remote_password = this.getProperty("remote_password");
        this.remote_port = this.getProperty("remote_port");
        this.remote_collection = this.getProperty("remote_collection");
    }

    /**
     * @return den remote_host
     */
    @Override
    public String getRemoteHost() {
        return remote_host;
    }

    /**
     * @return die remote_database
     */
    @Override
    public String getRemoteDatabase() {
        return remote_database;
    }

    /**
     * @return den remote_user
     */
    @Override
    public String getRemoteUser() {
        return remote_user;
    }

    /**
     * @return das remote_password
     */
    @Override
    public String getRemotePassword() {
        return remote_password;
    }

    /**
     * @return den remote_port
     */
    @Override
    public String getRemotePort() {
        return remote_port;
    }

    /**
     * @return die remote_collection
     */
    @Override
    public String getRemoteCollection() {
        return remote_collection;
    }
}
