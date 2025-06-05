package com.github.vasilangelov.hire4j.dto;

import java.util.Collection;

public class JobListingFilters {

    private String search = "";

    private Collection<String> tags;

    private String location = "";

    private Byte minYearsOfExperience;

    private int page = 1;

    public String getSearch() {
        return this.search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Collection<String> getTags() {
        return this.tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Byte getMinYearsOfExperience() {
        return this.minYearsOfExperience;
    }

    public void setMinYearsOfExperience(Byte minYearsOfExperience) {
        this.minYearsOfExperience = minYearsOfExperience;
    }

}
