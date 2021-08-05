package com.wucf.core.exception;

public class BaseException extends RuntimeException {
    public BaseException(Throwable e) {
        super(e.getMessage(), e);
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
