package com.github.vasilangelov.hire4j.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateUserRequest {

    @NotBlank(message = "First Name is required.")
    private String firstName;

    @NotBlank(message = "Last Name is required.")
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Password is required.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?.]).{6,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one special character, and be at least 6 characters long"
    )
    private String password;

    @NotBlank(message = "Confirm Password is required.")
    private String confirmPassword;

    public CreateUserRequest() {}

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return this.confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
