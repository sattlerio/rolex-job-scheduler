package com.sattler.exceptions;

/**
 * Created by ghovat on 06.11.17.
 */
public class RolexException extends Exception {
    public RolexException() {
    }

    public RolexException(String s) {
        super(s);
    }

    public RolexException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RolexException(Throwable throwable) {
        super(throwable);
    }
}
