package com.github.vasilangelov.hire4j.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public class JobApplicationRequest {

    @NotBlank(message = "CV is required.")
    private MultipartFile cv;

    private String coverLetter;

    public JobApplicationRequest() {}

    public MultipartFile getCv() {
        return this.cv;
    }

    public void setCv(MultipartFile cv) {
        this.cv = cv;
    }

    public String getCoverLetter() {
        return this.coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

}
