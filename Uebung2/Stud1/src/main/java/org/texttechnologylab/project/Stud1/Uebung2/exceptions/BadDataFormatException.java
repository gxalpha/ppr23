package org.texttechnologylab.project.Stud1.Uebung2.exceptions;

import org.texttechnologylab.project.exception.BundestagException;


/**
 * Exception, die geworfen wird, wenn die in einer BundestagFactory-Methode eingegeben Daten fehlerhaft sind (falsch formatiert, fehlende Daten, etc).
 */
public class BadDataFormatException extends BundestagException {
    /**
     * Konstruktor der Exception
     *
     * @param message Grund für die Exception
     */
    public BadDataFormatException(String message) {
        super(message);
    }

    /**
     * Konstruktor der Exception
     *
     * @param e Parent-Exception
     */
    public BadDataFormatException(Exception e) {
        super(e);
    }

    /**
     * Konstruktor der Exception
     *
     * @param message Grund für die Exception
     * @param e       Parent-Exception
     */
    public BadDataFormatException(String message, Exception e) {
        super(message, e);
    }
}
