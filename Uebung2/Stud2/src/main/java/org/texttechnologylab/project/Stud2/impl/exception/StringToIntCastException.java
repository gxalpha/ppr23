package org.texttechnologylab.project.Stud2.impl.exception;

import org.texttechnologylab.project.exception.BundestagException;

/**
 * Exception-Klasse für Strings, die nicht wunschgemäß zu Integer umgewandelt werden können
 *
 * @author Stud2
 */
public class StringToIntCastException extends BundestagException {
    public StringToIntCastException(String pMessage) {
        super(pMessage);
    }
}
