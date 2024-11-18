package org.texttechnologylab.project.Stud2.impl.exception;

import org.texttechnologylab.project.exception.BundestagException;

/**
 * Exception-Klasse für nicht gefundene Attribute von Bundestagsobjekten
 *
 * @author Stud2
 */
public class AttributeNotFoundError extends BundestagException {
    public AttributeNotFoundError(String pMessage) {
        super(pMessage);
    }
}
