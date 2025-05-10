package com.github.vasilangelov.hire4j.util.service;

public abstract class ServiceError {

    private final String message;

    public ServiceError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
