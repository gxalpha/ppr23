package org.texttechnologylab.project.Stud2.exception;

/**
 * Exception-Klasse für nicht identifizierbare Reden
 *
 * @author Stud2
 */
public class RedeNotFoundException extends BundestagException {
    public RedeNotFoundException(String pMessage) {
        super(pMessage);
    }
}
