package com.whl.quickjs.wrapper;

public class QuickJSException extends RuntimeException {
    public QuickJSException(String message) {
        super(message);
    }
    public QuickJSException(Throwable cause) {
        super(cause);
    }
    public QuickJSException(String message, Throwable cause) {
        super(message, cause);
    }
}
