package org.texttechnologylab.project.Stud1.Uebung3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.texttechnologylab.project.Stud1.data.BundestagFactory;
import org.texttechnologylab.project.Stud1.database.MongoDBConnectionHandler;
import org.texttechnologylab.project.Stud1.exceptions.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud1.exceptions.BadDataFormatException;

import java.io.IOException;

import static org.texttechnologylab.project.Stud1.Uebung3.TestHelper.readProtokolle;
import static org.texttechnologylab.project.Stud1.Uebung3.TestHelper.readStammdaten;

@DisplayName("Test for the upload to MongoDB")
public class UploadTest {

    @Test
    @DisplayName("Upload all data to MongoDB")
    public void uploadAll() throws BadDataFormatException, AbgeordneterNotFoundException {
        BundestagFactory factory = BundestagFactory.newInstance();
        readStammdaten(factory);
        readProtokolle(factory);

        MongoDBConnectionHandler handler;
        try {
            handler = new MongoDBConnectionHandler(MongoDBConnectionHandler.class.getClassLoader().getResourceAsStream("config.ini"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        handler.uploadEverything(factory);
    }
}
