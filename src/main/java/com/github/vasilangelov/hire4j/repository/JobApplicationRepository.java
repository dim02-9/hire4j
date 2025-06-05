package com.github.vasilangelov.hire4j.repository;

import com.github.vasilangelov.hire4j.dto.UserJobApplicationListItemView;
import com.github.vasilangelov.hire4j.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Boolean existsJobApplicationByUserIdAndJobListingId(Long userId, Long jobListingId);

    @Query("SELECT ja " +
            "FROM JobApplication ja " +
            "WHERE ja.user.email = :email " +
            "ORDER BY ja.jobListing.title ASC")
    Collection<UserJobApplicationListItemView> findAllByUserEmail(@Param("email") String email);

}
