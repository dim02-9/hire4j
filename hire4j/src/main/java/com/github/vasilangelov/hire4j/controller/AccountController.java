package com.github.vasilangelov.hire4j.controller;

import com.github.vasilangelov.hire4j.dto.CreateUserRequest;
import com.github.vasilangelov.hire4j.dto.UserJobApplicationListItemView;
import com.github.vasilangelov.hire4j.model.Role;
import com.github.vasilangelov.hire4j.service.JobApplicationService;
import com.github.vasilangelov.hire4j.service.UserService;
import com.github.vasilangelov.hire4j.util.BindingResultUtils;
import com.github.vasilangelov.hire4j.util.controller.AllowUser;
import com.github.vasilangelov.hire4j.util.service.ServiceResult;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;

@Controller
public class AccountController {

    private final UserService userService;
    private final JobApplicationService jobApplicationService;

    public AccountController(
            UserService userService,
            JobApplicationService jobApplicationService
    ) {
        this.userService = userService;
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping("/account/sign-in")
    public String signIn() {
        return "account/sign-in";
    }

    @GetMapping("/account/sign-up")
    public String signUp(Model model) {
        model.addAttribute("signUpRequest", new CreateUserRequest());

        return "account/sign-up";
    }

    @PostMapping("/account/sign-up")
    public String signUp(@Valid @ModelAttribute("signUpRequest") CreateUserRequest signUpRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "account/sign-up";
        }

        ServiceResult signUpServiceResult = this.userService.createUser(signUpRequest, Role.USER);

        if (!signUpServiceResult.isSuccess()) {
            BindingResultUtils.addResultErrorsToBindingResult(bindingResult, signUpServiceResult);

            return "account/sign-up";
        }

        return "redirect:/account/sign-in";
    }

    @AllowUser
    @PostMapping("/account/sign-in/success")
    public String handleSignInSuccess(String redirectURL) {
        if (redirectURL != null && !redirectURL.isBlank()) {
            return "redirect:" + redirectURL;
        }

        return "redirect:/";
    }

    @AllowUser
    @GetMapping("/account/applications")
    public String applications(Model model) {
        String userEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Collection<UserJobApplicationListItemView> jobApplications = this.jobApplicationService.getAllApplicationsForUser(userEmail);

        model.addAttribute("jobApplications", jobApplications);

        return "account/applications";
    }

}
