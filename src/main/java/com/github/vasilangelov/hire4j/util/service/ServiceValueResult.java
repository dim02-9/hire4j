package com.github.vasilangelov.hire4j.util.service;

import jakarta.annotation.Nullable;

import java.util.Collection;
import java.util.Optional;

public class ServiceValueResult<T> extends ServiceResult {

    @Nullable
    private final T value;

    public ServiceValueResult(Collection<ServiceError> errors) {
        super(errors);
        this.value = null;
    }

    public ServiceValueResult(T value) {
        super(null);
        this.value = value;
    }

    public static <T> ServiceValueResult<T> success(T value) {
        return new ServiceValueResult<>(value);
    }

    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

}
