package com.github.vasilangelov.hire4j.util.service;

import jakarta.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ServiceResult {

    @Nullable
    private final Collection<ServiceError> errors;

    protected ServiceResult(Collection<ServiceError> errors) {
        this.errors = errors;
    }

    public static ServiceResult success() {
        return new ServiceResult(null);
    }

    public static ServiceResult failure(String message) {
        return new ServiceResult(List.of(new ServiceGeneralError(message)));
    }

    public static ServiceResult failure(String fieldName, String message) {
        return new ServiceResult(List.of(new ServiceValidationError(fieldName, message)));
    }

    public static ServiceResult failure(Collection<ServiceError> errors) {
        return new ServiceResult(errors);
    }

    public boolean isSuccess() {
        return this.errors == null || this.errors.isEmpty();
    }

    public Collection<ServiceError> getErrors() {
        if (this.errors == null) {
            return List.of();
        }

        return Collections.unmodifiableCollection(this.errors);
    }

    public Collection<ServiceGeneralError> getGeneralErrors() {
        if (this.errors == null) {
            return List.of();
        }

        return this.errors
                .stream()
                .filter(error -> error instanceof ServiceGeneralError)
                .map(error -> (ServiceGeneralError) error)
                .toList();
    }

    public Collection<ServiceValidationError> getValidationErrors() {
        if (this.errors == null) {
            return List.of();
        }

        return this.errors
                .stream()
                .filter(error -> error instanceof ServiceValidationError)
                .map(error -> (ServiceValidationError) error)
                .toList();
    }

}
