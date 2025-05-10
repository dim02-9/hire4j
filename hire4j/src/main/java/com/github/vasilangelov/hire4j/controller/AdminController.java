package com.github.vasilangelov.hire4j.controller;

import com.github.vasilangelov.hire4j.dto.*;
import com.github.vasilangelov.hire4j.model.Organization;
import com.github.vasilangelov.hire4j.model.Role;
import com.github.vasilangelov.hire4j.repository.OrganizationRepository;
import com.github.vasilangelov.hire4j.repository.UserRepository;
import com.github.vasilangelov.hire4j.service.OrganizationService;
import com.github.vasilangelov.hire4j.service.UserService;
import com.github.vasilangelov.hire4j.util.BindingResultUtils;
import com.github.vasilangelov.hire4j.util.RedirectAttributesUtils;
import com.github.vasilangelov.hire4j.util.controller.AllowAdmin;
import com.github.vasilangelov.hire4j.util.controller.AllowSuperAdmin;
import com.github.vasilangelov.hire4j.util.service.ServiceResult;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Optional;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final OrganizationRepository organizationRepository;
    private final OrganizationService organizationService;

    public AdminController(UserRepository userRepository, UserService userService, OrganizationRepository organizationRepository, OrganizationService organizationService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.organizationRepository = organizationRepository;
        this.organizationService = organizationService;
    }

    @AllowAdmin
    @GetMapping("/admin/users")
    public String users(
            @RequestParam(defaultValue = "") String email,
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        Page<UserView> users = this.userRepository.findAllByEmailStartingWith(email, PageRequest.of(Math.max(page - 1, 0), 10, Sort.by("email").ascending()));

        model.addAttribute("users", users);

        return "admin/users";
    }

    @AllowSuperAdmin
    @PostMapping("/admin/delete-user")
    public String deleteUser(String email, RedirectAttributes redirectAttributes) {
        ServiceResult deleteUserResult = this.userService.deleteUserByEmail(email);

        if (!deleteUserResult.isSuccess()) {
            RedirectAttributesUtils.bindServiceResultErrors(redirectAttributes, deleteUserResult);

            return "redirect:/admin/users";
        }

        redirectAttributes.addFlashAttribute("info", "User deleted successfully.");

        return "redirect:/admin/users";
    }

    @AllowSuperAdmin
    @GetMapping("/admin/create-admin")
    public String createAdmin(Model model) {
        model.addAttribute("createUserRequest", new CreateUserRequest());

        return "admin/create-admin";
    }

    @AllowSuperAdmin
    @PostMapping("/admin/create-admin")
    public String createAdmin(@Valid @ModelAttribute("createUserRequest") CreateUserRequest createUserRequest, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/create-admin";
        }

        ServiceResult createAdminServiceResult = this.userService.createUser(createUserRequest, Role.ADMIN);

        if (!createAdminServiceResult.isSuccess()) {
            BindingResultUtils.addResultErrorsToBindingResult(bindingResult, createAdminServiceResult);

            return "/admin/create-admin";
        }

        redirectAttributes.addFlashAttribute("info", "Admin created successfully.");

        return "redirect:/admin/users";
    }

    @AllowAdmin
    @GetMapping("/admin/organizations")
    public String organizations(@RequestParam(defaultValue = "") String organization, @RequestParam(defaultValue = "1") int page, Model model) {
        Page<OrganizationWithMaintainersView> organizations = this.organizationRepository.findByNameStartingWithIgnoreCase(organization, PageRequest.of(Math.max(page - 1, 0), 10, Sort.by("name").ascending()));

        model.addAttribute("organizations", organizations);

        return "admin/organizations";
    }

    @AllowAdmin
    @GetMapping("/admin/create-organization")
    public String createOrganization(Model model) {
        model.addAttribute("organization", new CreateOrganizationRequest());

        return "admin/create-organization";
    }

    @AllowAdmin
    @PostMapping("/admin/create-organization")
    public String createOrganization(@Valid @ModelAttribute("organization") CreateOrganizationRequest createOrganizationRequest, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/create-organization";
        }

        ServiceResult serviceResult = this.organizationService.createOrganization(createOrganizationRequest);

        if (!serviceResult.isSuccess()) {
            BindingResultUtils.addResultErrorsToBindingResult(bindingResult, serviceResult);

            return "/admin/create-organization";
        }

        redirectAttributes.addFlashAttribute("info", "Organization created successfully.");

        return "redirect:/admin/organizations";
    }

    @AllowAdmin
    @GetMapping("/admin/edit-organization/{id}")
    public String editOrganization(@PathVariable Long id, Model model) {
        Optional<Organization> organization = this.organizationRepository.findById(id);

        if (organization.isEmpty()) {
            return "redirect:/admin/organizations";
        }

        EditOrganizationRequest editOrganizationRequest = new EditOrganizationRequest(organization.get().getName(), organization.get().getDescription());

        model.addAttribute("organizationId", id);
        model.addAttribute("organization", editOrganizationRequest);

        return "admin/edit-organization";
    }

    @AllowAdmin
    @PostMapping("/admin/edit-organization/{id}")
    public String editOrganization(
            @PathVariable Long id,
            @Valid @ModelAttribute("organization") EditOrganizationRequest editOrganizationRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/admin/edit-organization";
        }

        Organization organization = this.organizationRepository
                .findById(id)
                .map((organizationToEdit) -> {
                    organizationToEdit.setName(editOrganizationRequest.getName());
                    organizationToEdit.setDescription(editOrganizationRequest.getDescription());

                    return organizationToEdit;
                })
                .orElse(null);

        if (organization == null) {
            redirectAttributes.addFlashAttribute("errors", Collections.singleton("Organization does not exist."));

            return "redirect:/admin/organizations";
        }

        this.organizationRepository.save(organization);

        redirectAttributes.addFlashAttribute("info", "Organization edited successfully.");

        return "redirect:/admin/organizations";
    }

    @AllowAdmin
    @PostMapping("/admin/delete-organization")
    public String deleteOrganization(Long id, RedirectAttributes redirectAttributes) {
        if (!this.organizationRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errors", Collections.singleton("Organization does not exist."));

            return "redirect:/admin/organizations";
        }

        this.organizationRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("info", "Organization deleted successfully.");

        return "redirect:/admin/organizations";
    }

    @AllowAdmin
    @PostMapping("/admin/add-organization-maintainer")
    public String addOrganizationMaintainer(Long id, String email, RedirectAttributes redirectAttributes) {
        ServiceResult addMaintainerByEmailResult = this.organizationService.addMaintainerByEmail(id, email);

        if (!addMaintainerByEmailResult.isSuccess()) {
            RedirectAttributesUtils.bindServiceResultErrors(redirectAttributes, addMaintainerByEmailResult);

            return "redirect:/admin/organizations";
        }

        redirectAttributes.addFlashAttribute("info", "User added to organization successfully.");

        return "redirect:/admin/organizations";
    }

    @AllowAdmin
    @PostMapping("/admin/remove-organization-maintainer")
    public String removeOrganizationMaintainer(Long id, String email, RedirectAttributes redirectAttributes) {
        ServiceResult removeMaintainerFromOrganizationResult = this.organizationService.removeMaintainerFromOrganization(id, email);

        if (!removeMaintainerFromOrganizationResult.isSuccess()) {
            RedirectAttributesUtils.bindServiceResultErrors(redirectAttributes, removeMaintainerFromOrganizationResult);

            return "redirect:/admin/organizations";
        }

        redirectAttributes.addFlashAttribute("info", "Maintainer removed from organization successfully.");

        return "redirect:/admin/organizations";
    }

}
