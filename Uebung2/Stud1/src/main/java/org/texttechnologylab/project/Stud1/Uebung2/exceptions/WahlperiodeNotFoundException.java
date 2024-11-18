package org.texttechnologylab.project.Stud1.Uebung2.exceptions;

import org.texttechnologylab.project.exception.BundestagException;


/**
 * Exception, die geworfen wird, wenn benötigte Daten von einer BundestagFactory-Methode null sind.
 */
public class WahlperiodeNotFoundException extends BundestagException {
    /**
     * Konstruktor der Exception
     *
     * @param message Grund für die Exception
     */
    public WahlperiodeNotFoundException(String message) {
        super(message);
    }

    /**
     * Konstruktor der Exception
     *
     * @param e Parent-Exception
     */
    public WahlperiodeNotFoundException(Exception e) {
        super(e);
    }
}
