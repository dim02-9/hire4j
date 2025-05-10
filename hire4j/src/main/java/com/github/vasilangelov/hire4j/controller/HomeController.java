package com.github.vasilangelov.hire4j.controller;

import com.github.vasilangelov.hire4j.dto.JobListingDetailsView;
import com.github.vasilangelov.hire4j.service.JobListingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class HomeController {

    private final JobListingService jobListingService;

    public HomeController(JobListingService jobListingService) {
        this.jobListingService = jobListingService;
    }

    @GetMapping("/")
    public String index(Model model) {
        Collection<JobListingDetailsView> latestJobListings = this.jobListingService.getLatestJobListings(3);

        model.addAttribute("latestJobListings", latestJobListings);

        return "home/index";
    }

}
