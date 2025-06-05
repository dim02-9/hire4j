package com.github.vasilangelov.hire4j.model;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "job_listings")
public class JobListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 3000)
    private String description;

    @Column(nullable = true)
    private Byte minYearsOfExperience;

    @Column(nullable = true)
    private String location;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToMany
    private Set<JobListingTag> tags;

    @OneToMany(mappedBy = "jobListing", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<JobApplication> applications;

    public JobListing() { }

    public JobListing(
            String title,
            String description,
            Byte minYearsOfExperience,
            String location,
            ZonedDateTime createdAt,
            Organization organization,
            Set<JobListingTag> tags
    ) {
        this.title = title;
        this.description = description;
        this.minYearsOfExperience = minYearsOfExperience;
        this.location = location;
        this.createdAt = createdAt;
        this.organization = organization;
        this.tags = tags;
        this.applications = Set.of();
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
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

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Set<JobListingTag> getTags() {
        return this.tags;
    }

    public void setTags(Set<JobListingTag> tags) {
        this.tags = tags;
    }

    public Set<JobApplication> getApplications() {
        return this.applications;
    }

    public void setApplications(Set<JobApplication> applications) {
        this.applications = applications;
    }

}
