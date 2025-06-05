package com.github.vasilangelov.hire4j.controller;

import com.github.vasilangelov.hire4j.dto.JobApplicationRequest;
import com.github.vasilangelov.hire4j.dto.JobListingDetailsView;
import com.github.vasilangelov.hire4j.dto.JobListingFilters;
import com.github.vasilangelov.hire4j.dto.JobListingFiltersView;
import com.github.vasilangelov.hire4j.service.JobApplicationService;
import com.github.vasilangelov.hire4j.service.JobListingService;
import com.github.vasilangelov.hire4j.util.BindingResultUtils;
import com.github.vasilangelov.hire4j.util.controller.AllowUser;
import com.github.vasilangelov.hire4j.util.service.ServiceResult;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;

@Controller
public class JobListingsController {

    private final JobListingService jobListingService;
    private final JobApplicationService jobApplicationService;

    public JobListingsController(
            JobListingService jobListingService,
            JobApplicationService jobApplicationService
    ) {
        this.jobListingService = jobListingService;
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping("/job-listings")
    public String index(
            @ModelAttribute JobListingFilters filters,
            Model model
    ) {
        JobListingFiltersView filtersView = this.jobListingService.getJobListingFilters(filters);

        Page<JobListingDetailsView> page = this.jobListingService.searchJobListings(filters);

        model.addAttribute("activeTagsSet", new HashSet<>(filters.getTags() != null ? filters.getTags() : List.of()));
        model.addAttribute("filters", filtersView);
        model.addAttribute("page", page);

        return "job-listings/index";
    }

    @GetMapping("/job-listings/{id}")
    public String details(@PathVariable Long id, Model model) {
        JobListingDetailsView jobListing = this.jobListingService.findJobListingDetailsView(id);

        if (jobListing == null) {
            return "redirect:/job-listings";
        }

        model.addAttribute("jobApplicationRequest", new JobApplicationRequest());
        model.addAttribute("jobListing", jobListing);

        return "job-listings/details";
    }

    @AllowUser
    @PostMapping("/job-listings/{id}/apply")
    public String apply(
            @PathVariable Long id,
            @ModelAttribute JobApplicationRequest jobApplicationRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        JobListingDetailsView jobListing = this.jobListingService.findJobListingDetailsView(id);

        if (jobListing == null) {
            return "redirect:/job-listings";
        }

        model.addAttribute("jobListing", jobListing);

        if (bindingResult.hasErrors()) {

            return "job-listings/details";
        }

        String loggedInUserEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        ServiceResult jobApplicationServiceResult = this.jobApplicationService.applyForJobListing(
                id,
                loggedInUserEmail,
                jobApplicationRequest
        );

        if (!jobApplicationServiceResult.isSuccess()) {
            BindingResultUtils.addResultErrorsToBindingResult(bindingResult, jobApplicationServiceResult);

            return "job-listings/details";
        }

        redirectAttributes.addFlashAttribute("info", "You have successfully applied for the job listing.");

        return "redirect:/job-listings/" + id;
    }

}
