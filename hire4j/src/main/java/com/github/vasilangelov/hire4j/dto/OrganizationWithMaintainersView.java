package com.github.vasilangelov.hire4j.dto;

import java.util.List;

public interface OrganizationWithMaintainersView {

    long getId();

    String getName();

    String getDescription();

    List<UserView> getMaintainers();

}
