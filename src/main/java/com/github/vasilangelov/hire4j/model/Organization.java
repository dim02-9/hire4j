package com.github.vasilangelov.hire4j.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(length = 1000)
    private String description;

    @OneToMany
    @JoinColumn(name = "organization_id")
    private Set<User> maintainers;

    @OneToMany
    @JoinColumn(name = "organization_id")
    private Set<JobListing> jobListings;

    public Organization() {}

    public Organization(String name, String description, Set<User> maintainers) {
        this.name = name;
        this.description = description;
        this.maintainers = maintainers;
    }

    public Organization(String name, String description, Set<User> maintainers, Set<JobListing> jobListings) {
        this(name, description, maintainers);
        this.jobListings = jobListings;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<User> getMaintainers() {
        return this.maintainers;
    }

    public void setMaintainers(Set<User> maintainers) {
        this.maintainers = maintainers;
    }

    public Set<JobListing> getJobListings() {
        return this.jobListings;
    }

    public void setJobListings(Set<JobListing> jobListings) {
        this.jobListings = jobListings;
    }
    
}
