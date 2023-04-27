package org.ligson.ichat.ex;

public class InnerException extends RuntimeException {
    public InnerException(String error) {
        super(error);
    }

    public InnerException(Throwable error) {
        super(error);
    }
}
