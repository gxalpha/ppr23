package org.texttechnologylab.project.Stud1.website;

import java.time.Instant;
import java.util.Map;

/**
 * Ein Log-Eintrag
 */
public interface LogEntry {
    /**
     * Timestamp des Log-Eintrags
     *
     * @return Der Timestamp
     */
    Instant getTimestamp();

    /**
     * Gibt die aufgerufene Route zurück
     *
     * @return Route
     */
    String getRoute();

    /**
     * Gibt alle Parameter zurück (Key: Parameter, Value: Wert)
     *
     * @return Map mit allen Parametern
     */
    Map<String, String> getParams();

    /**
     * Gibt die Parameter formatiert zurück
     *
     * @return String der Parameter
     */
    String getParamsFormatted();
}
