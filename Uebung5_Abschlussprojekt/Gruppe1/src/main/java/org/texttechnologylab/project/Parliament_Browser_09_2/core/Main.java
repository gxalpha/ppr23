package org.texttechnologylab.project.Parliament_Browser_09_2.core;

import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.MongoDBHandlerImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.parsing.SessionParserThread;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.SparkHelper;

import java.io.IOException;

/**
 * Dummy Main-Klasse für Ordnerstruktur
 *
 * @author Stud
 */
public class Main {
    /**
     * @param args Program arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        MongoDBHandler mongoDBHandler = new MongoDBHandlerImpl("Project_09_02.txt");
        SparkHelper.startup(mongoDBHandler);
        SessionParserThread.init(mongoDBHandler);
    }
}
