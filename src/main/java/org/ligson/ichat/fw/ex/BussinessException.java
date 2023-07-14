package org.ligson.ichat.fw.ex;

public class BussinessException extends RuntimeException {
    public BussinessException(String error) {
        super(error);
    }

    public BussinessException(Throwable error) {
        super(error);
    }
}
