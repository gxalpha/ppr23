package org.texttechnologylab.project.Stud2.exceptions;

/**
 * Exception-Klasse für nicht identifizierbare Reden
 *
 * @author Stud2
 */
public class RedeNotFoundException extends BundestagException {
    public RedeNotFoundException(String message) {
        super(message);
    }
}
