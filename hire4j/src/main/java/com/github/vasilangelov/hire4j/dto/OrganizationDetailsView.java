package com.github.vasilangelov.hire4j.dto;

import java.util.Collection;

public interface OrganizationDetailsView {

    Long getId();

    String getName();

    String getDescription();

    Collection<OrganizationJobListingTableItemView> getJobListings();

}
