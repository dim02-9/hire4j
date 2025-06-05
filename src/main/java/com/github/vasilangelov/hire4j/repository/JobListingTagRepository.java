package com.github.vasilangelov.hire4j.repository;

import com.github.vasilangelov.hire4j.model.JobListingTag;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface JobListingTagRepository extends CrudRepository<JobListingTag, Long> {

    Collection<JobListingTag> findAllByNormalizedNameIn(Collection<String> normalizedNames);

}
