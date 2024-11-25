package org.texttechnologylab.project.Parliament_Browser_09_2.parsing;

import com.mongodb.client.model.Filters;
import org.apache.uima.UIMAException;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Rede;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Sitzung;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.SitzungImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.RedeDB;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.SitzungDB;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.RedeDBImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.SitzungDBImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.downloads.ProtocolDownloader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SessionParserThread {

    private static final List<Integer> analyzedSessions19;
    private static final List<Integer> analyzedSessions20;
    private static boolean initialized;
    // Hacky but eh. Let's not make a map of maps or something.
    private static Map<Integer, String> knownSessions19;
    private static Map<Integer, String> knownSessions20;
    private static MongoDBHandler mongoDBHandler;
    private static boolean running;
    private static Thread runner;
    // Things are just getting worse.
    private static String formattedProtocolProgress;

    static {
        initialized = false;
        knownSessions19 = Map.of();
        knownSessions20 = Map.of();
        analyzedSessions19 = new ArrayList<>();
        analyzedSessions20 = new ArrayList<>();
        running = false;
        runner = null;
        //noinspection ResultOfMethodCallIgnored
        NLPAnalyzer.isReady();
    }

    /**
     * Private constructor, don't use.
     *
     * @author Stud
     */
    private SessionParserThread() {
        throw new RuntimeException("Don't even try.");
    }

    public synchronized static void init(MongoDBHandler handler) throws IOException {
        mongoDBHandler = handler;
        List<Sitzung> sitzungen = new ArrayList<>();
        mongoDBHandler.getMongoDatabase().getCollection("sitzungen").find()
                .map(SitzungImpl::new)
                .forEach((Consumer<? super Sitzung>) sitzungen::add);
        analyzedSessions19.addAll(sitzungen.stream().filter(sitzung -> sitzung.getWahlperiode() == 19).map(Sitzung::getSitzungsnummer).collect(Collectors.toSet()));
        analyzedSessions20.addAll(sitzungen.stream().filter(sitzung -> sitzung.getWahlperiode() == 20).map(Sitzung::getSitzungsnummer).collect(Collectors.toSet()));

        fetchAllSessions();

        initialized = true;
    }

    private static void fetchAllSessions() throws IOException {
        knownSessions19 = ProtocolDownloader.fetchAllProtocolLinks(19);
        knownSessions20 = ProtocolDownloader.fetchAllProtocolLinks(20);
    }

    public static double getTotalProgress() {
        return (double) (analyzedSessions19.size() + analyzedSessions20.size()) / (double) (knownSessions19.size() + knownSessions20.size());
    }

    public static String getFormattedProtocolProgress() {
        return formattedProtocolProgress;
    }

    public static boolean isReady() {
        return NLPAnalyzer.isReady() && initialized;
    }

    public static boolean isRunning() {
        return running || runner != null;
    }

    private static void abortAsync() {
        new Thread(() -> {
            try {
                stop();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public synchronized static void start() {
        if (!isReady()) {
            // TODO: Maybe throw error
            return;
        }

        if (running) {
            return;
        }

        runner = new Thread(() -> {
            while (running) {
                Optional<Integer> optionalWp19 = knownSessions19.keySet()
                        .stream()
                        .filter(Predicate.not(analyzedSessions19::contains))
                        .findFirst();
                Optional<Integer> optionalWp20 = knownSessions20.keySet()
                        .stream()
                        .filter(Predicate.not(analyzedSessions20::contains))
                        .findFirst();

                String url;
                int wp;
                int sessionNumber;
                if (optionalWp19.isPresent()) {
                    sessionNumber = optionalWp19.get();
                    url = knownSessions19.get(sessionNumber);
                    wp = 19;
                } else if (optionalWp20.isPresent()) {
                    sessionNumber = optionalWp20.get();
                    url = knownSessions20.get(sessionNumber);
                    wp = 20;
                } else {
                    abortAsync();
                    return;
                }

                String protocolString;
                try {
                    protocolString = ProtocolDownloader.downloadProtocol(url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                List<Rede> reden;
                Sitzung sitzung;
                try {
                    Document document = XMLParser.getDocument(protocolString);
                    reden = ProtocolParser.parseAllRedenDocument(document);
                    sitzung = ProtocolParser.parseSitzung(document);
                } catch (ParserConfigurationException | SAXException | ParseException e) {
                    throw new RuntimeException(e);
                }

                int gesamtanzahlReden = reden.size();
                int analysierteReden = 0;
                RedeDB redeDB = new RedeDBImpl(mongoDBHandler);
                while (!reden.isEmpty()) {
                    if (!running) {
                        abortAsync();
                        return;
                    }

                    Rede rede = reden.remove(0);
                    formattedProtocolProgress = "Sitzung " + sitzung.getID() + ", Rede " + (analysierteReden + 1) + " von " + gesamtanzahlReden;
                    try {
                        // TODO Write function to find by ID in RedeDB
                        if (rede.getID().isEmpty() || rede.getRednerID().isEmpty() || rede.getRednerID().startsWith("99") || mongoDBHandler.getMongoDatabase().getCollection("reden").find(Filters.eq("_id", rede.getID())).first() != null) {
                            // Rede ohne Redner oder bereits analysiert
                            analysierteReden++;
                            continue;
                        }

                        Abgeordneter redner = mongoDBHandler.getAbgeordneterByID(rede.getRednerID());
                        if (redner == null) {
                            analysierteReden++;
                            continue;
                        }

                        NLPAnalyzer.analyze(rede);
                        redeDB.insertRedeDB(rede, redner);
                    } catch (UIMAException | IOException | SAXException e) {
                        throw new RuntimeException(e);
                    }
                    analysierteReden++;
                }

                SitzungDB sitzungDB = new SitzungDBImpl(mongoDBHandler);
                sitzungDB.insertSitzungDB(List.of(sitzung));
                if (wp == 19) {
                    analyzedSessions19.add(sessionNumber);
                } else {
                    analyzedSessions20.add(sessionNumber);
                }
            }
        });
        running = true;
        runner.start();
    }

    public synchronized static void stop() throws InterruptedException {
        if (!isReady()) {
            // TODO: Maybe throw error
            return;
        }
        if (!running) {
            return;
        }

        running = false;
        if (runner != null) {
            runner.join();
            runner = null;
        }
    }
}
