package com.example.library.api.exceptions.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorMessage {

    private final String error;
    private final String message;
    private final Integer statusCode;

    public ErrorMessage(Exception exception, Integer statusCode) {
        this.error = exception.getClass().getSimpleName();
        this.message = exception.getMessage();
        this.statusCode = statusCode;
    }

}