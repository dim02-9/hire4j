package com.github.vasilangelov.hire4j.service;

import com.github.vasilangelov.hire4j.dto.CreateUserRequest;
import com.github.vasilangelov.hire4j.model.Role;
import com.github.vasilangelov.hire4j.model.User;
import com.github.vasilangelov.hire4j.repository.RoleRepository;
import com.github.vasilangelov.hire4j.repository.UserRepository;
import com.github.vasilangelov.hire4j.util.service.ServiceError;
import com.github.vasilangelov.hire4j.util.service.ServiceResult;
import com.github.vasilangelov.hire4j.util.service.ServiceValidationError;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ServiceResult createUser(CreateUserRequest createUserRequest, String role) {
        boolean passwordsMatch = createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword());

        List<ServiceError> serviceErrors = new ArrayList<>();

        if (!passwordsMatch) {
            serviceErrors.add(new ServiceValidationError("confirmPassword", "Passwords do not match."));
        }

        boolean userWithEmailAlreadyExists = this.userRepository.existsByEmail(createUserRequest.getEmail());

        if (userWithEmailAlreadyExists) {
            serviceErrors.add(new ServiceValidationError("email", "User with this email already exists."));
        }

        if (!serviceErrors.isEmpty()) {
            return ServiceResult.failure(serviceErrors);
        }

        Role userRole = this.roleRepository.findByName(role).orElseThrow();

        User user = new User(
                createUserRequest.getEmail(),
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                this.passwordEncoder.encode(createUserRequest.getPassword()),
                userRole
        );

        this.userRepository.save(user);

        return ServiceResult.success();
    }

    public void createSuperAdmin(String email, String password) {
        Role superAdminRole = this.roleRepository.findByName(Role.SUPER_ADMIN).orElseThrow();

        this.userRepository.save(new User(email, "Super", "Admin", this.passwordEncoder.encode(password), superAdminRole));
    }

    public ServiceResult deleteUserByEmail(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return ServiceResult.failure("User with this email does not exist.");
        }

        if (Objects.equals(user.get().getRole().getName(), Role.SUPER_ADMIN)) {
            return ServiceResult.failure("Super Admin role cannot be deleted.");
        }

        this.userRepository.deleteUserByEmail(email);

        return ServiceResult.success();
    }

}
