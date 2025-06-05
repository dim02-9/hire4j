package com.github.vasilangelov.hire4j.dto;

import org.springframework.beans.factory.annotation.Value;

public interface OrganizationJobListingTableItemView {

    Long getId();

    String getTitle();

    String getDescription();

    @Value("#{target.applications.?[status == T(com.github.vasilangelov.hire4j.model.JobApplicationStatus).REVIEWING].size()}")
    Integer getNewApplicationsCount();

}
