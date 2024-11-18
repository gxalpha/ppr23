package org.texttechnologylab.project.Stud1.exceptions;


/**
 * Exception, die geworfen wird, wenn ein Abgeordneter in einer BundestagFactory-Methode referenziert wird, der aber noch nicht bekannt ist.
 */
public class AbgeordneterNotFoundException extends Exception {
    /**
     * Konstruktor der Exception
     *
     * @param message Grund für die Exception
     */
    public AbgeordneterNotFoundException(String message) {
        super(message);
    }
}
