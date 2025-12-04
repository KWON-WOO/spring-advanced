package org.example.expert.config;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ExceptionEnum exception;
    private final String message;

    public CustomException(ExceptionEnum exception) {
        super(exception.getMessage());
        this.exception = exception;
        this.message = exception.getMessage();
    }
    public CustomException(ExceptionEnum exception, String message) {
        this.exception = exception;
        this.message = message;
    }
}
