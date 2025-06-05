package com.github.vasilangelov.hire4j.service;

import com.github.vasilangelov.hire4j.model.Organization;
import com.github.vasilangelov.hire4j.model.User;
import com.github.vasilangelov.hire4j.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ThymeleafService {

    private final HttpServletRequest request;
    private final RoleHierarchy roleHierarchy;
    private final UserRepository userRepository;

    public ThymeleafService(HttpServletRequest request, RoleHierarchy roleHierarchy, UserRepository userRepository) {
        this.request = request;
        this.roleHierarchy = roleHierarchy;
        this.userRepository = userRepository;
    }

    public String getRequestURI() {
        return this.request.getRequestURI();
    }

    public boolean isInRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        return this.roleHierarchy
                .getReachableGrantedAuthorities(authentication.getAuthorities())
                .contains(new SimpleGrantedAuthority(roleName));
    }

    public Optional<Organization> getUserOrganization() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        String email = authentication.getName();

        Optional<User> user = this.userRepository.findByEmail(email);

        return user.map(User::getOrganization);
    }

}
