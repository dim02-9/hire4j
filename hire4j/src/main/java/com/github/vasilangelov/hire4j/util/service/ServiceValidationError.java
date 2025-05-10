package com.github.vasilangelov.hire4j.util.service;

public class ServiceValidationError extends ServiceError {

    private final String fieldName;

    public ServiceValidationError(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

}
