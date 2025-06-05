package com.github.vasilangelov.hire4j.dto;

import com.github.vasilangelov.hire4j.model.JobApplicationStatus;
import org.springframework.beans.factory.annotation.Value;

public interface JobApplicationListItemView {

    Long getId();

    @Value("#{target.user.firstName}")
    String getUserFirstName();

    @Value("#{target.user.lastName}")
    String getUserLastName();

    @Value("#{target.user.email}")
    String getUserEmail();

    JobApplicationStatus getStatus();

    String getCoverLetter();

    default String getUserFullName() {
        return getUserFirstName() + " " + getUserLastName();
    }

    default String getNormalizedStatus() {
        return getStatus().name();
    }

}
