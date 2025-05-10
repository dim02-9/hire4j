package com.github.vasilangelov.hire4j.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateJobListingRequest {

    @NotBlank(message = "Title is required.")
    @Size(max = 255, message = "Title must be at most 255 characters long.")
    private String title;

    @NotBlank(message = "Description is required.")
    @Size(max = 3000, message = "Description must be max 3000 characters long.")
    private String description;

    private Byte minYearsOfExperience;

    @Size(max = 255, message = "Location must be at most 255 characters long.")
    private String location;

    @Size(min = 1, message = "At least one tag is required.")
    private String[] tags;

    public CreateJobListingRequest() {
        this.tags = new String[0];
    }

    public CreateJobListingRequest(String title, String description, Byte minYearsOfExperience, String location, String[] tags) {
        this.title = title;
        this.description = description;
        this.minYearsOfExperience = minYearsOfExperience;
        this.location = location;
        this.tags = tags;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte getMinYearsOfExperience() {
        return this.minYearsOfExperience;
    }

    public void setMinYearsOfExperience(Byte minYearsOfExperience) {
        this.minYearsOfExperience = minYearsOfExperience;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String[] getTags() {
        return this.tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

}
