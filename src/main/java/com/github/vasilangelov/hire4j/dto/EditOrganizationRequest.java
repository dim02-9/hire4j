package com.github.vasilangelov.hire4j.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EditOrganizationRequest {

    @NotBlank(message = "Name is required.")
    @Size(max = 255, message = "Name must be at most 255 characters long.")
    private String name;

    @NotBlank(message = "Description is required.")
    @Size(max = 1000, message = "Name must be at most 255 characters long.")
    private String description;

    public EditOrganizationRequest() {}

    public EditOrganizationRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
