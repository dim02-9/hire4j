package com.github.vasilangelov.hire4j.seeder;

import com.github.vasilangelov.hire4j.model.Role;
import com.github.vasilangelov.hire4j.repository.RoleRepository;
import com.github.vasilangelov.hire4j.util.Seeder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RoleSeeder extends Seeder {

    private static final String[] roles = { Role.SUPER_ADMIN, Role.ADMIN, Role.USER };

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    protected void seed() {
        Set<String> existingRoles = new HashSet<>(this.roleRepository.findAll().stream().map(Role::getName).toList());

        List<Role> rolesToCreate = Arrays.stream(roles).filter(roleName -> !existingRoles.contains(roleName)).map(Role::new).toList();

        if (!rolesToCreate.isEmpty()) {
            this.roleRepository.saveAll(rolesToCreate);
        }
    }

}
