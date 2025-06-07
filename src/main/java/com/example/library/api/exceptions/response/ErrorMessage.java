package com.example.library.api.errors;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
class ErrorMessage {

    private final String error;
    private final String message;
    private final Integer statusCode;

    ErrorMessage(Exception exception, Integer statusCode) {
        this.error = exception.getClass().getSimpleName();
        this.message = exception.getMessage();
        this.statusCode = statusCode;
    }

}