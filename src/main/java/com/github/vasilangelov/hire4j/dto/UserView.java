package com.github.vasilangelov.hire4j.dto;

import com.github.vasilangelov.hire4j.model.Role;
import org.springframework.beans.factory.annotation.Value;

public interface UserView {

    public String getFirstName();

    public String getLastName();

    public String getEmail();

    @Value("#{target.role.name}")
    public String getRoleName();

    default String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    default String getNormalizedRoleName() {
        return Role.getNormalizedRoleName(getRoleName());
    }

}
