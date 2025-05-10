package com.github.vasilangelov.hire4j.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "job_applications",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "job_listing_id"})
        }
)
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_listing_id", nullable = false)
    private JobListing jobListing;

    @Column(nullable = false)
    private JobApplicationStatus status;

    @Column(nullable = false)
    private String cvPath;

    @Column(nullable = true, length = 3000)
    private String coverLetter;

    public JobApplication() {}

    public JobApplication(
            User user,
            JobListing jobListing,
            JobApplicationStatus status,
            String cvPath,
            String coverLetter
    ) {
        this.user = user;
        this.jobListing = jobListing;
        this.status = status;
        this.cvPath = cvPath;
        this.coverLetter = coverLetter;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JobListing getJobListing() {
        return this.jobListing;
    }

    public void setJobListing(JobListing jobListing) {
        this.jobListing = jobListing;
    }

    public JobApplicationStatus getStatus() {
        return this.status;
    }

    public void setStatus(JobApplicationStatus status) {
        this.status = status;
    }

    public String getCvPath() {
        return this.cvPath;
    }

    public void setCvPath(String cvName) {
        this.cvPath = cvName;
    }

    public String getCoverLetter() {
        return this.coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

}
