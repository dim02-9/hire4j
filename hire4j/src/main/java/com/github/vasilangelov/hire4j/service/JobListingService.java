package com.github.vasilangelov.hire4j.service;

import com.github.vasilangelov.hire4j.dto.*;
import com.github.vasilangelov.hire4j.model.*;
import com.github.vasilangelov.hire4j.repository.JobListingRepository;
import com.github.vasilangelov.hire4j.repository.OrganizationRepository;
import com.github.vasilangelov.hire4j.util.service.ServiceResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Set;

@Service
public class JobListingService {

    private final JobListingRepository jobListingRepository;
    private final OrganizationRepository organizationRepository;
    private final JobListingTagService jobListingTagService;

    public JobListingService(
            JobListingRepository jobListingRepository,
            OrganizationRepository organizationRepository,
            JobListingTagService jobListingTagService
    ) {
        this.jobListingRepository = jobListingRepository;
        this.organizationRepository = organizationRepository;
        this.jobListingTagService = jobListingTagService;
    }

    public ServiceResult createJobListing(long organizationId, CreateJobListingRequest request) {
        Organization organization = this.organizationRepository.findById(organizationId).orElse(null);

        if (organization == null) {
            return ServiceResult.failure("Organization not found");
        }

        Set<JobListingTag> tags = this.jobListingTagService.upsertTags(request.getTags());

        this.jobListingRepository.save(new JobListing(
                request.getTitle(),
                request.getDescription(),
                request.getMinYearsOfExperience(),
                request.getLocation(),
                ZonedDateTime.now(),
                organization,
                tags
        ));

        return ServiceResult.success();
    }

    public ServiceResult editJobListing(Long id, CreateJobListingRequest request) {
        JobListing jobListing = this.jobListingRepository.findById(id).orElse(null);

        if (jobListing == null) {
            return ServiceResult.failure("Job listing not found");
        }

        Set<JobListingTag> tags = this.jobListingTagService.upsertTags(request.getTags());

        jobListing.setTitle(request.getTitle());
        jobListing.setDescription(request.getDescription());
        jobListing.setMinYearsOfExperience(request.getMinYearsOfExperience());
        jobListing.setLocation(request.getLocation());
        jobListing.setTags(tags);

        this.jobListingRepository.save(jobListing);

        return ServiceResult.success();
    }

    public void removeJobListing(Long id) {
        this.jobListingRepository.deleteById(id);
    }

    public JobListingDetailsView findJobListingDetailsView(Long id) {
        return this.jobListingRepository.findDetailsViewById(id).orElse(null);
    }

    public JobListingFiltersView getJobListingFilters(JobListingFilters filters) {
        Collection<TagWithCountView> tags = this.jobListingRepository
                .getJobListingTagsBy(
                        filters.getSearch(),
                        filters.getLocation(),
                        filters.getMinYearsOfExperience()
                );

        Collection<LocationWithCountView> locations = this.jobListingRepository
                .getJobListingLocationsBy(
                        filters.getSearch(),
                        filters.getTags(),
                        filters.getMinYearsOfExperience()
                );

        return new JobListingFiltersView(tags, locations);
    }

    public Page<JobListingDetailsView> searchJobListings(JobListingFilters filters) {
        int page = Math.max(0, filters.getPage() - 1);

        return this.jobListingRepository
                .findPageBy(
                        filters.getSearch(),
                        filters.getTags(),
                        filters.getLocation(),
                        filters.getMinYearsOfExperience(),
                        PageRequest.of(page, 10)
                );
    }

    public Collection<JobListingDetailsView> getLatestJobListings(int count) {
        return this.jobListingRepository.findLatestJobListings(count);
    }

}
