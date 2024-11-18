package org.texttechnologylab.project.Stud1.Uebung2.exceptions;

import org.texttechnologylab.project.exception.BundestagException;


/**
 * Exception, die geworfen wird, wenn ein Abgeordneter in einer BundestagFactory-Methode referenziert wird, der aber noch nicht bekannt ist.
 */
public class AbgeordneterNotFoundException extends BundestagException {
    /**
     * Konstruktor der Exception
     *
     * @param message Grund für die Exception
     */
    public AbgeordneterNotFoundException(String message) {
        super(message);
    }
}
