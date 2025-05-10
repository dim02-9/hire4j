package com.github.vasilangelov.hire4j.seeder;

import com.github.vasilangelov.hire4j.dto.SuperuserConfig;
import com.github.vasilangelov.hire4j.repository.UserRepository;
import com.github.vasilangelov.hire4j.service.UserService;
import com.github.vasilangelov.hire4j.util.Seeder;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder extends Seeder {

    private final UserRepository userRepository;
    private final UserService userService;

    private final SuperuserConfig appSuperuserConfig;

    public UserSeeder(UserRepository userRepository, UserService userService, SuperuserConfig appSuperuserConfig) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.appSuperuserConfig = appSuperuserConfig;
    }

    @Override
    protected void seed() {
        if (this.userRepository.existsByEmail(this.appSuperuserConfig.getEmail())) {
            return;
        }

        this.userService.createSuperAdmin(
                this.appSuperuserConfig.getEmail(),
                this.appSuperuserConfig.getPassword());
    }

}
