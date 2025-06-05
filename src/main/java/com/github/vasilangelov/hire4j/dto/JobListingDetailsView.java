package com.github.vasilangelov.hire4j.dto;

import com.github.vasilangelov.hire4j.model.JobApplicationStatus;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.Comparator;

public interface JobListingDetailsView {

    Long getId();

    @Value("#{target.organization.id}")
    Long getOrganizationId();

    @Value("#{target.organization.name}")
    String getOrganizationName();

    String getTitle();

    String getDescription();

    Byte getMinYearsOfExperience();

    String getLocation();

    @Value("#{target.tags.![displayName]}")
    Collection<String> getTags();

    Collection<JobApplicationListItemView> getApplications();

    default Collection<String> getSortedTags() {
        return this.getTags().stream().sorted().toList();
    }

    default Collection<JobApplicationListItemView> getSortedApplications() {
        return this.getApplications()
                .stream()
                .sorted(Comparator.comparing(JobApplicationListItemView::getUserFullName))
                .sorted((a, b) -> {
                    if (a.getStatus() == b.getStatus()) {
                        return 0;
                    }
                    if (a.getStatus() == JobApplicationStatus.REVIEWING) {
                        return 1;
                    }
                    return -1;
                })
                .toList();
    }

}
