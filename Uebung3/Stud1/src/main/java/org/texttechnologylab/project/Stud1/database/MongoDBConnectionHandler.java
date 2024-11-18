package org.texttechnologylab.project.Stud1.database;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.texttechnologylab.project.Stud1.data.BundestagFactory;
import org.texttechnologylab.project.Stud1.data.impl.Abgeordneter_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.Rede_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.Sitzung_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.Tagesordnung_MongoDB_Impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Connection-Handler for our Datebase
 */
public class MongoDBConnectionHandler {
    private final MongoClient client;
    private final MongoDatabase database;

    /**
     * Erstellt einen MongoDBConnectionHandler
     *
     * @param dbConfig Konfigurationsdatei für die Datenbank
     * @throws IOException Bei Fehlern mit der Konfigurationsdatei
     */
    public MongoDBConnectionHandler(InputStream dbConfig) throws IOException {
        MongoProperties properties = new MongoProperties(dbConfig);

        client = MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(List.of(new ServerAddress(properties.getRemoteHost(), properties.getRemotePort()))))
                .credential(MongoCredential.createScramSha1Credential(properties.getRemoteUser(), properties.getRemoteDatabase(), properties.getRemotePassword().toCharArray()))
                .build()
        );

        database = client.getDatabase(properties.getRemoteDatabase());
    }

    /**
     * Lädt alles der Factory in die Datenbank hoch
     *
     * @param factory Factory
     */
    public void uploadEverything(BundestagFactory factory) {
        deleteEverything();

        /* Abgeordnete */
        MongoCollection<Document> abgeordnete = database.getCollection(Abgeordneter_MongoDB_Impl.Keys.COLLECTION_NAME);
        abgeordnete.insertMany(factory.listAbgeordnete().stream().map(MongoDBConversionHelper::toDocument).collect(Collectors.toList()));

        /* Reden */
        MongoCollection<Document> reden = database.getCollection(Rede_MongoDB_Impl.Keys.COLLECTION_NAME);
        reden.insertMany(factory.listReden().stream().map(MongoDBConversionHelper::toDocument).collect(Collectors.toList()));

        /* Sitzungen */
        MongoCollection<Document> sitzung = database.getCollection(Sitzung_MongoDB_Impl.Keys.COLLECTION_NAME);
        sitzung.insertMany(factory.listSitzungen().stream().map(MongoDBConversionHelper::toDocument).collect(Collectors.toList()));

        /* Tagesordnungen */
        MongoCollection<Document> tagesordnung = database.getCollection(Tagesordnung_MongoDB_Impl.Keys.COLLECTION_NAME);
        tagesordnung.insertMany(factory.listTagesordnungen().stream().map(MongoDBConversionHelper::toDocument).collect(Collectors.toList()));
    }

    /**
     * Löscht alle Collections der Datenbank
     */
    public void deleteEverything() {
        database.listCollectionNames().forEach((Consumer<? super String>) name -> database.getCollection(name).drop());
    }

    /**
     * Gibt die Datenbank zurück
     *
     * @return Die Datenbank
     */
    public MongoDatabase getDatabase() {
        return database;
    }
}
