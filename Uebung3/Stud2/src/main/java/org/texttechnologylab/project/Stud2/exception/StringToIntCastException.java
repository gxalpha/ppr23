package org.texttechnologylab.project.Stud2.exception;

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
