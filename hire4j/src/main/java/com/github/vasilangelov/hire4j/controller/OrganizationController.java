package com.github.vasilangelov.hire4j.controller;

import com.github.vasilangelov.hire4j.dto.CreateJobListingRequest;
import com.github.vasilangelov.hire4j.dto.EditOrganizationRequest;
import com.github.vasilangelov.hire4j.dto.JobListingDetailsView;
import com.github.vasilangelov.hire4j.dto.OrganizationDetailsView;
import com.github.vasilangelov.hire4j.model.JobApplication;
import com.github.vasilangelov.hire4j.model.Organization;
import com.github.vasilangelov.hire4j.repository.OrganizationRepository;
import com.github.vasilangelov.hire4j.repository.UserRepository;
import com.github.vasilangelov.hire4j.service.JobApplicationService;
import com.github.vasilangelov.hire4j.service.JobListingService;
import com.github.vasilangelov.hire4j.util.BindingResultUtils;
import com.github.vasilangelov.hire4j.util.controller.AllowUser;
import com.github.vasilangelov.hire4j.util.service.ServiceResult;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrganizationController {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final JobListingService jobListingService;
    private final JobApplicationService jobApplicationService;

    public OrganizationController(
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            JobListingService jobListingService,
            JobApplicationService jobApplicationService
    ) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.jobListingService = jobListingService;
        this.jobApplicationService = jobApplicationService;
    }

    @AllowUser
    @GetMapping("/organization/{id}")
    public String index(@PathVariable Long id, Model model) {
        if (!this.isInOrganization(id)) {
            return "redirect:/";
        }

        OrganizationDetailsView organization = this.organizationRepository.findViewById(id).orElse(null);

        if (organization == null) {
            return "redirect:/";
        }

        EditOrganizationRequest editOrganizationRequest = new EditOrganizationRequest(organization.getName(), organization.getDescription());

        model.addAttribute("organization", organization);
        model.addAttribute("editOrganizationRequest", editOrganizationRequest);
        model.addAttribute("createJobListingRequest", new CreateJobListingRequest());

        return "organization/index";
    }

    @AllowUser
    @PostMapping("/organization/{id}/edit")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute("editOrganizationRequest") EditOrganizationRequest editOrganizationRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!this.isInOrganization(id)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            OrganizationDetailsView organization = this.organizationRepository.findViewById(id).orElse(null);

            model.addAttribute("organization", organization);
            model.addAttribute("editOrganizationRequest", editOrganizationRequest);
            model.addAttribute("createJobListingRequest", new CreateJobListingRequest());

            return "organization/index";
        }

        this.organizationRepository
                .findById(id)
                .ifPresent(organization -> {
                    organization.setName(editOrganizationRequest.getName());
                    organization.setDescription(editOrganizationRequest.getDescription());
                    this.organizationRepository.save(organization);
                });

        redirectAttributes.addFlashAttribute("info", "Organization updated successfully.");

        return "redirect:/organization/" + id;
    }

    @AllowUser
    @PostMapping("/organization/{id}/create-job-listing")
    public String createJobListing(
            @PathVariable Long id,
            @Valid @ModelAttribute("createJobListingRequest") CreateJobListingRequest createJobListingRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!this.isInOrganization(id)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            OrganizationDetailsView organization = this.organizationRepository.findViewById(id).orElse(null);

            model.addAttribute("organization", organization);
            model.addAttribute("editOrganizationRequest", new EditOrganizationRequest());
            model.addAttribute("createJobListingRequest", createJobListingRequest);

            return "organization/index";
        }

        ServiceResult createJobListingServiceResult = this.jobListingService.createJobListing(id, createJobListingRequest);

        if (!createJobListingServiceResult.isSuccess()) {
            BindingResultUtils.addResultErrorsToBindingResult(bindingResult, createJobListingServiceResult);

            return "redirect:/organization/" + id;
        }

        redirectAttributes.addFlashAttribute("info", "Job listing created successfully.");

        return "redirect:/organization/" + id;
    }

    @AllowUser
    @PostMapping("/organization/{id}/remove-job-listing")
    public String removeJobListing(
            @PathVariable Long id,
            Long jobListingId,
            RedirectAttributes redirectAttributes) {
        if (!this.isInOrganization(id)) {
            return "redirect:/";
        }

        this.jobListingService.removeJobListing(jobListingId);

        redirectAttributes.addFlashAttribute("info", "Job listing removed successfully.");

        return "redirect:/organization/" + id;
    }

    @AllowUser
    @GetMapping("/organization/{organizationId}/job-listing/{jobListingId}")
    public String jobListing(
            @PathVariable Long organizationId,
            @PathVariable Long jobListingId,
            Model model) {
        if (!this.isInOrganization(organizationId)) {
            return "redirect:/";
        }

        JobListingDetailsView jobListingDetailsView = this.jobListingService.findJobListingDetailsView(jobListingId);

        if (jobListingDetailsView == null) {
            return "redirect:/organization/" + organizationId;
        }

        CreateJobListingRequest editJobListingRequest =
                new CreateJobListingRequest(
                        jobListingDetailsView.getTitle(),
                        jobListingDetailsView.getDescription(),
                        jobListingDetailsView.getMinYearsOfExperience(),
                        jobListingDetailsView.getLocation(),
                        jobListingDetailsView.getTags().toArray(new String[0])
                );

        model.addAttribute("editJobListingRequest", editJobListingRequest);
        model.addAttribute("jobListing", jobListingDetailsView);

        return "organization/job-listing";
    }

    @AllowUser
    @PostMapping("/organization/{organizationId}/job-listing/{jobListingId}/edit")
    public String editJobListing(
            @PathVariable Long organizationId,
            @PathVariable Long jobListingId,
            @Valid @ModelAttribute("editJobListingRequest") CreateJobListingRequest editJobListingRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (!this.isInOrganization(organizationId)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            JobListingDetailsView jobListingDetailsView = this.jobListingService.findJobListingDetailsView(jobListingId);

            model.addAttribute("jobListing", jobListingDetailsView);

            return "organization/job-listing";
        }

        ServiceResult editJobListingServiceResult = this.jobListingService.editJobListing(jobListingId, editJobListingRequest);

        if (!editJobListingServiceResult.isSuccess()) {
            BindingResultUtils.addResultErrorsToBindingResult(bindingResult, editJobListingServiceResult);

            JobListingDetailsView jobListingDetailsView = this.jobListingService.findJobListingDetailsView(jobListingId);

            model.addAttribute("jobListing", jobListingDetailsView);

            return "organization/job-listing";
        }

        redirectAttributes.addFlashAttribute("info", "Job listing updated successfully.");

        return "redirect:/organization/" + organizationId + "/job-listing/" + jobListingId;
    }

    @AllowUser
    @GetMapping("/organization/{organizationId}/application/{applicationId}/cv")
    public ResponseEntity<Resource> downloadCv(
            @PathVariable Long organizationId,
            @PathVariable Long applicationId
    ) {
        if (!this.isInOrganization(organizationId)) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        Organization organization = this.organizationRepository.findById(organizationId).orElse(null);
        JobApplication jobApplication = this.jobApplicationService.getJobApplicationById(applicationId).orElse(null);

        if (organization == null || jobApplication == null || !jobApplication.getJobListing().getOrganization().getId().equals(organizationId)) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        Resource resource = this.jobApplicationService
                .getCVAsResource(applicationId)
                .orElse(null);

        if (resource == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @AllowUser
    @GetMapping("/organization/{organizationId}/job-listing/{jobListingId}/application/{applicationId}/accept")
    public String acceptApplication(
            @PathVariable Long organizationId,
            @PathVariable Long jobListingId,
            @PathVariable Long applicationId,
            RedirectAttributes redirectAttributes
    ) {
        if (!this.isInOrganization(organizationId)) {
            return "redirect:/";
        }

        ServiceResult acceptApplicationServiceResult = this.jobApplicationService.acceptApplication(applicationId);

        if (!acceptApplicationServiceResult.isSuccess()) {
            redirectAttributes.addFlashAttribute("error", acceptApplicationServiceResult);
        } else {
            redirectAttributes.addFlashAttribute("info", "Application accepted successfully.");
        }

        return "redirect:/organization/" + organizationId + "/job-listing/" + jobListingId;
    }

    @AllowUser
    @GetMapping("/organization/{organizationId}/job-listing/{jobListingId}/application/{applicationId}/reject")
    public String rejectApplication(
            @PathVariable Long organizationId,
            @PathVariable Long jobListingId,
            @PathVariable Long applicationId,
            RedirectAttributes redirectAttributes
    ) {
        if (!this.isInOrganization(organizationId)) {
            return "redirect:/";
        }

        ServiceResult rejectApplicationServiceResult = this.jobApplicationService.rejectApplication(applicationId);

        if (!rejectApplicationServiceResult.isSuccess()) {
            redirectAttributes.addFlashAttribute("error", rejectApplicationServiceResult);
        } else {
            redirectAttributes.addFlashAttribute("info", "Application rejected successfully.");
        }

        return "redirect:/organization/" + organizationId + "/job-listing/" + jobListingId;
    }

    private boolean isInOrganization(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        String email = authentication.getName();

        return this.userRepository
                .findByEmail(email)
                .map(user -> user.getOrganization().getId().equals(id))
                .orElse(false);
    }

}
