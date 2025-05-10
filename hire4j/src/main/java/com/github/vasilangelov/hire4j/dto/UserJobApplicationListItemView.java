package com.github.vasilangelov.hire4j.dto;

import com.github.vasilangelov.hire4j.model.JobApplicationStatus;
import org.springframework.beans.factory.annotation.Value;

public interface UserJobApplicationListItemView {

    Long getId();

    JobApplicationStatus getStatus();

    @Value("#{target.jobListing.title}")
    String getJobListingTitle();

    @Value("#{target.jobListing.organization.name}")
    String getJobListingOrganizationName();

}
