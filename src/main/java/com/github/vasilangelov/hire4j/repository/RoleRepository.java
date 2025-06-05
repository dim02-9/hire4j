package com.github.vasilangelov.hire4j.repository;

import com.github.vasilangelov.hire4j.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Byte> {

    Optional<Role> findByName(String name);

}
