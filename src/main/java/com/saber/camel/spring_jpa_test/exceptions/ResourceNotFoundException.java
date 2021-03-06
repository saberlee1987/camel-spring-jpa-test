package com.saber.camel.spring_jpa_test.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    private String message;

    public ResourceNotFoundException( String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
