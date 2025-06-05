package com.github.vasilangelov.hire4j.dto;

import java.util.Collection;

public class JobListingFiltersView {

    private Collection<TagWithCountView> tagsWithCount;

    private Collection<LocationWithCountView> locationsWithCount;

    public JobListingFiltersView(
            Collection<TagWithCountView> tagsWithCount,
            Collection<LocationWithCountView> locationsWithCount
    ) {
        this.tagsWithCount = tagsWithCount;
        this.locationsWithCount = locationsWithCount;
    }

    public Collection<TagWithCountView> getTagsWithCount() {
        return this.tagsWithCount;
    }

    public void setTagsWithCount(Collection<TagWithCountView> tagsWithCount) {
        this.tagsWithCount = tagsWithCount;
    }

    public Collection<LocationWithCountView> getLocationsWithCount() {
        return this.locationsWithCount;
    }

    public void setLocationsWithCount(Collection<LocationWithCountView> locationsWithCount) {
        this.locationsWithCount = locationsWithCount;
    }

    public Long getAllLocationsCount() {
        return this.locationsWithCount
                .stream()
                .mapToLong(LocationWithCountView::getCount)
                .sum();
    }

}
