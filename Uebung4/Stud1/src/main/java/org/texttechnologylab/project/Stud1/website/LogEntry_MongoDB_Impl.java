package org.texttechnologylab.project.Stud1.website;

import org.bson.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementierung des Log-Eintrags
 */
public class LogEntry_MongoDB_Impl implements LogEntry {

    private final Instant timestamp;
    private final String route;
    private final Map<String, String> params;

    /**
     * Konstruktor zum Erstellen eines neuen Log-Eintrags.
     * Timestamp wird automatisch erstellt.
     *
     * @param route  Route
     * @param params Parameter
     */
    public LogEntry_MongoDB_Impl(String route, Map<String, String> params) {
        this.timestamp = Instant.now();
        this.route = route;
        this.params = params;
    }

    /**
     * Konstruktor zum Auslesen aus der Datenbank.
     *
     * @param document MongoDB-Document
     */
    public LogEntry_MongoDB_Impl(Document document) {
        this.timestamp = Instant.ofEpochSecond(document.getLong(Keys.TIMESTAMP));
        this.route = document.getString(Keys.ROUTE);
        this.params = new HashMap<>();
        ((Document) document.get(Keys.PARAMS)).forEach((key, value) -> this.params.put(key, (String) value));
    }

    /**
     * Timestamp als Epoch Unix Timestamp (Sekunden seit 01.01.1970)
     *
     * @return Der Timestamp
     */
    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Gibt die aufgerufene Route zurück
     *
     * @return Route
     */
    @Override
    public String getRoute() {
        return route;
    }

    /**
     * Gibt alle Parameter zurück (Key: Parameter, Value: Wert)
     *
     * @return Map mit allen Parametern
     */
    @Override
    public Map<String, String> getParams() {
        return params;
    }

    /**
     * Gibt die Parameter formatiert zurück
     *
     * @return Liste der Parameter
     */
    @Override
    public String getParamsFormatted() {
        if (params.isEmpty()) {
            return "<em>Keine Parameter</em>";
        }

        AtomicReference<String> string = new AtomicReference<>("");
        AtomicInteger i = new AtomicInteger();
        params.forEach((key, value) -> {
            String innerString = string.get();
            if (i.get() != 0) {
                innerString += ", ";
            }
            innerString += "\"" + key + "\": \"" + value + "\"";
            i.getAndIncrement();
            string.set(innerString);
        });
        return string.get();
    }

    /**
     * Hilfsklasse zum Vermeiden von Typos.
     */
    public static class Keys {
        public static final String COLLECTION_NAME = "log";
        public static final String TIMESTAMP = "timestamp";
        public static final String ROUTE = "route";
        public static final String PARAMS = "params";
    }

}
