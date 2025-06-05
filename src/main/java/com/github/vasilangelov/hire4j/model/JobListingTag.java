package com.github.vasilangelov.hire4j.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "job_listing_tags")
public class JobListingTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String normalizedName;

    @Column(nullable = false)
    private String displayName;

    @ManyToMany
    private Set<JobListing> jobListings;

    public JobListingTag() {}

    public JobListingTag(String normalizedName, String displayName) {
        this.normalizedName = normalizedName;
        this.displayName = displayName;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNormalizedName() {
        return this.normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<JobListing> getJobListings() {
        return this.jobListings;
    }

    public void setJobListings(Set<JobListing> jobListings) {
        this.jobListings = jobListings;
    }

}
