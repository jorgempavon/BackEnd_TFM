package com.example.library.api.exceptions.models;

public class InternalServerErrorException extends RuntimeException {
    private static final String DESCRIPTION = "Internal Server Exception";

    public InternalServerErrorException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }

}
