package com.healthplus.healthplus_api.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException() {
        super();
    }
}
