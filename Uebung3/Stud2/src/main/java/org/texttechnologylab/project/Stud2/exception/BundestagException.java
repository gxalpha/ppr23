package org.texttechnologylab.project.Stud2.exception;

public class BundestagException extends Exception{
    public BundestagException() {
    }

    public BundestagException(Throwable pCause) {
        super(pCause);
    }

    public BundestagException(String pMessage) {
        super(pMessage);
    }

    public BundestagException(String pMessage, Throwable pCause) {
        super(pMessage, pCause);
    }
}
