package com.github.vasilangelov.hire4j.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateOrganizationRequest extends EditOrganizationRequest {

    @NotBlank(message = "An organization must have at least one maintainer.")
    @Email
    private String maintainerEmail;

    public String getMaintainerEmail() {
        return this.maintainerEmail;
    }

    public void setMaintainerEmail(String maintainerEmail) {
        this.maintainerEmail = maintainerEmail;
    }

}
