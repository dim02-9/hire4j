package com.github.vasilangelov.hire4j.service;

import com.github.vasilangelov.hire4j.dto.CreateOrganizationRequest;
import com.github.vasilangelov.hire4j.model.Organization;
import com.github.vasilangelov.hire4j.model.User;
import com.github.vasilangelov.hire4j.repository.OrganizationRepository;
import com.github.vasilangelov.hire4j.repository.UserRepository;
import com.github.vasilangelov.hire4j.util.service.ServiceResult;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public OrganizationService(OrganizationRepository organizationRepository, UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    public ServiceResult createOrganization(CreateOrganizationRequest request) {
        Optional<User> maintainer = this.userRepository.findByEmail(request.getMaintainerEmail());

        if (maintainer.isEmpty()) {
            return ServiceResult.failure("maintainerEmail", request.getMaintainerEmail());
        }

        Organization organization = maintainer.get().getOrganization();

        if (organization != null) {
            return ServiceResult.failure("maintainerEmail", "User is already a maintainer of another organization.");
        }

        this.organizationRepository.save(new Organization(request.getName(), request.getDescription(), Collections.singleton(maintainer.get())));

        return ServiceResult.success();
    }

    public ServiceResult addMaintainerByEmail(Long organizationId, String email) {
        Organization organization = this.organizationRepository.findById(organizationId).orElse(null);

        if (organization == null) {
            return ServiceResult.failure("Organization does not exist.");
        }

        User user = this.userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ServiceResult.failure("User does not exist.");
        }

        if (user.getOrganization() != null) {
            return ServiceResult.failure("User is already a maintainer of another organization.");
        }

        organization.getMaintainers().add(user);

        this.organizationRepository.save(organization);

        return ServiceResult.success();
    }

    public ServiceResult removeMaintainerFromOrganization(Long organizationId, String email) {
        Organization organization = this.organizationRepository.findById(organizationId).orElse(null);

        if (organization == null) {
            return ServiceResult.failure("Organization does not exist.");
        }

        User user = organization
                .getMaintainers()
                .stream()
                .filter(maintainer -> maintainer.getEmail().equals(email))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return ServiceResult.failure("User does not exist.");
        }

        if (!organization.getMaintainers().remove(user)) {
            return ServiceResult.failure("User is not a maintainer of the organization.");
        }

        this.organizationRepository.save(organization);

        return ServiceResult.success();
    }

}
