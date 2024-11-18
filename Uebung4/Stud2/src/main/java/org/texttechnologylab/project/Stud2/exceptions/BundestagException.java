package org.texttechnologylab.project.Stud2.exceptions;

/**
 * Klasse für eigene Exceptions
 *
 * @author Stud2
 */
public class BundestagException extends Exception{
    public BundestagException() {}

    public BundestagException(Throwable cause) {
        super(cause);
    }

    public BundestagException(String message) {
        super(message);
    }

    public BundestagException(String message, Throwable cause) {
        super(message, cause);
    }
}
