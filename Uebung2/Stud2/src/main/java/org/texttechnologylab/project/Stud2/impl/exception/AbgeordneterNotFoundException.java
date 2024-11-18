package org.texttechnologylab.project.Stud2.impl.exception;

import org.texttechnologylab.project.exception.BundestagException;

/**
 * Exception-Klasse für nicht identifizierbare Abgeordnete
 *
 * @author Stud2
 */
public class AbgeordneterNotFoundException extends BundestagException {
    public AbgeordneterNotFoundException() {
        super("Der Abgeordnete konnte nicht identifiziert werden.");
    }
    public AbgeordneterNotFoundException(String pMessage) {
        super(pMessage);
    }
}
