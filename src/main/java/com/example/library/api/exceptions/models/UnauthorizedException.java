package com.example.library.api.exceptions.models;

public class UnauthorizedException  extends RuntimeException {
    private static final String DESCRIPTION = "Unauthorized Exception";

    public UnauthorizedException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }
}