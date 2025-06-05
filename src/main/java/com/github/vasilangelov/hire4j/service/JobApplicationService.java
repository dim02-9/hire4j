package com.github.vasilangelov.hire4j.service;

import com.github.vasilangelov.hire4j.dto.JobApplicationRequest;
import com.github.vasilangelov.hire4j.dto.UserJobApplicationListItemView;
import com.github.vasilangelov.hire4j.model.JobApplication;
import com.github.vasilangelov.hire4j.model.JobApplicationStatus;
import com.github.vasilangelov.hire4j.model.JobListing;
import com.github.vasilangelov.hire4j.model.User;
import com.github.vasilangelov.hire4j.repository.JobApplicationRepository;
import com.github.vasilangelov.hire4j.repository.JobListingRepository;
import com.github.vasilangelov.hire4j.repository.UserRepository;
import com.github.vasilangelov.hire4j.util.service.ServiceResult;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobListingRepository jobListingRepository;
    private final UserRepository userRepository;

    public JobApplicationService(
            JobApplicationRepository jobApplicationRepository,
            JobListingRepository jobListingRepository,
            UserRepository userRepository
    ) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobListingRepository = jobListingRepository;
        this.userRepository = userRepository;
    }

    public ServiceResult applyForJobListing(
            Long jobListingId,
            String userEmail,
            JobApplicationRequest request
    ) {
        JobListing jobListing = this.jobListingRepository.findById(jobListingId).orElse(null);

        if (jobListing == null) {
            return ServiceResult.failure("Job listing not found.");
        }

        User user = this.userRepository.findByEmail(userEmail).orElse(null);

        if (user == null) {
            return ServiceResult.failure("User not found.");
        }

        if (request.getCv() == null || request.getCv().isEmpty()) {
            return ServiceResult.failure("cv", "CV is required.");
        }

        if (!Objects.equals(request.getCv().getContentType(), "application/pdf")) {
            return ServiceResult.failure("cv","CV must be a PDF file.");
        }

        if (request.getCv().getSize() > 5 * 1024 * 1024) {
            return ServiceResult.failure("cv","CV must be less than 5MB.");
        }

        String cvFilename = String.format("CV-%s-%s.pdf", jobListing.getId(), user.getId());

        Path cvPath = Path.of("target", "uploads", "cv")
                .toAbsolutePath()
                .normalize();

        Path cvFilePath;

        try {
            Files.createDirectories(cvPath);

            cvFilePath = cvPath.resolve(cvFilename);

            Files.copy(request.getCv().getInputStream(), cvFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            return ServiceResult.failure("cv", "Failed to save CV.");
        }

        JobApplication jobApplication = new JobApplication(
                user,
                jobListing,
                JobApplicationStatus.REVIEWING,
                cvFilePath.toString(),
                request.getCoverLetter()
        );

        this.jobApplicationRepository.save(jobApplication);

        return ServiceResult.success();
    }

    public boolean loggedInUserHasAppliedForJobListing(Long jobListingId) {
        String userEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = this.userRepository.findByEmail(userEmail).orElse(null);

        if (user == null) {
            return false;
        }

        return this.jobApplicationRepository.existsJobApplicationByUserIdAndJobListingId(user.getId(), jobListingId);
    }

    public Optional<Resource> getCVAsResource(Long jobApplicationId) {
        JobApplication jobApplication = this.jobApplicationRepository.findById(jobApplicationId).orElse(null);

        if (jobApplication == null) {
            return Optional.empty();
        }

        try {
            Resource resource = new UrlResource(Paths.get(jobApplication.getCvPath()).normalize().toUri());

            if (resource.exists()) {
                return Optional.of(resource);
            }

            return Optional.empty();
        } catch (MalformedURLException exception) {
            return Optional.empty();
        }
    }

    public ServiceResult acceptApplication(Long jobApplicationId) {
        Optional<JobApplication> jobApplicationOptional = this.jobApplicationRepository.findById(jobApplicationId);

        if (jobApplicationOptional.isEmpty()) {
            return ServiceResult.failure("Job application not found.");
        }

        JobApplication jobApplication = jobApplicationOptional.get();

        jobApplication.setStatus(JobApplicationStatus.ACCEPTED);

        this.jobApplicationRepository.save(jobApplication);

        return ServiceResult.success();
    }

    public ServiceResult rejectApplication(Long jobApplicationId) {
        Optional<JobApplication> jobApplicationOptional = this.jobApplicationRepository.findById(jobApplicationId);

        if (jobApplicationOptional.isEmpty()) {
            return ServiceResult.failure("Job application not found.");
        }

        JobApplication jobApplication = jobApplicationOptional.get();

        jobApplication.setStatus(JobApplicationStatus.REJECTED);

        this.jobApplicationRepository.save(jobApplication);

        return ServiceResult.success();
    }

    public Collection<UserJobApplicationListItemView> getAllApplicationsForUser(String userEmail) {
        return this.jobApplicationRepository.findAllByUserEmail(userEmail);
    }

    public Optional<JobApplication> getJobApplicationById(Long jobApplicationId) {
        return this.jobApplicationRepository.findById(jobApplicationId);
    }

}
