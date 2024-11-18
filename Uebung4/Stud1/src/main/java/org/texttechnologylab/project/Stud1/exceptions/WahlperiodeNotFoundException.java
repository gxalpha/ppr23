package org.texttechnologylab.project.Stud1.exceptions;


/**
 * Exception, die geworfen wird, wenn benötigte Daten von einer BundestagFactory-Methode null sind.
 */
public class WahlperiodeNotFoundException extends Exception {
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
