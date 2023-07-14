package org.ligson.ichat.fw.ex;

public class InnerException extends RuntimeException {
    public InnerException(String error) {
        super(error);
    }

    public InnerException(Throwable error) {
        super(error);
    }
}
