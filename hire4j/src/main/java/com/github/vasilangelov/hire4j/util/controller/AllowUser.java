package com.github.vasilangelov.hire4j.util.controller;

import com.github.vasilangelov.hire4j.model.Role;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('" + Role.USER + "')")
public @interface AllowUser {
}
