package org.texttechnologylab.project.Stud1.core;

import org.texttechnologylab.project.Stud1.database.MongoDBConnectionHandler;
import org.texttechnologylab.project.Stud1.nlp.DUUIHelper;
import org.texttechnologylab.project.Stud1.nlp.NLPAnalyzer;
import org.texttechnologylab.project.Stud1.util.Static;
import org.texttechnologylab.project.Stud1.website.SparkHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int autoclose_port = 0;
        if (args.length == 0) {
            Static.ENABLE_NLP = true;
        } else {
            try {
                autoclose_port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }

        System.out.println("Initializing MongoDB...");
        MongoDBConnectionHandler handler;
        try {
            handler = new MongoDBConnectionHandler(new FileInputStream("config.ini"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("MongoDB is ready!");

        System.out.println("Starting Spark...");
        SparkHelper.startup(handler);
        System.out.println("Spark is ready!");

        System.out.println("Loading NLP progress...");
        NLPAnalyzer.initProgress(handler);
        System.out.println("NLP progress loaded.");

        if (Static.ENABLE_NLP) {
            System.out.println("Initializing DUUI...");
            while (!DUUIHelper.isReady()) {
                Thread.sleep(100);
            }
            System.out.println("DUUI is ready!");
        } else {
            System.out.println("Deployment detected - not running DUUI.");

            if (autoclose_port != 0) {
                try {
                    // Verbinde mit ServerSocket, beende das Programm bei der ersten Nachricht (aka: Sobald der Socket geschlossen wird).
                    Socket socket = new Socket("localhost", autoclose_port);
                    socket.getInputStream().read();
                    System.exit(0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
