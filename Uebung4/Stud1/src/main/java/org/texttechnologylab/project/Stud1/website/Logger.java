package org.texttechnologylab.project.Stud1.website;

import org.texttechnologylab.project.Stud1.database.MongoDBConnectionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Hilfsklasse zum Loggen
 */
public class Logger {
    private final MongoDBConnectionHandler handler;

    /**
     * Konstruktor
     *
     * @param handler Konstruktor
     */
    public Logger(MongoDBConnectionHandler handler) {
        this.handler = handler;
    }

    /**
     * Logs a request
     *
     * @param route  The route
     * @param params The params used in the request
     */
    public void log(String route, Map<String, String> params) {
        handler.uploadLogEntry(new LogEntry_MongoDB_Impl(route, params));
    }

    /**
     * @return Liste aller Log-Einträge
     */
    public List<LogEntry> getEntries() {
        List<LogEntry> entries = new ArrayList<>();
        handler.getDatabase().getCollection(LogEntry_MongoDB_Impl.Keys.COLLECTION_NAME).find().map(LogEntry_MongoDB_Impl::new).forEach((Consumer<? super LogEntry_MongoDB_Impl>) entries::add);
        return entries;
    }
}
