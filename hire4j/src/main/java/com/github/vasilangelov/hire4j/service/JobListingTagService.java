package com.github.vasilangelov.hire4j.service;

import com.github.vasilangelov.hire4j.model.JobListingTag;
import com.github.vasilangelov.hire4j.repository.JobListingTagRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobListingTagService {

    private final JobListingTagRepository jobListingTagRepository;

    public JobListingTagService(JobListingTagRepository jobListingTagRepository) {
        this.jobListingTagRepository = jobListingTagRepository;
    }

    public Set<JobListingTag> upsertTags(String[] tagNames) {
        Collection<JobListingTag> tags = this.jobListingTagRepository.findAllByNormalizedNameIn(Arrays.stream(tagNames).map(String::toLowerCase).collect(Collectors.toSet()));
        Collection<JobListingTag> newTags = new HashSet<>();

        for (String tagName : tagNames) {
            if (tags.stream().noneMatch((tag) -> tag.getNormalizedName().equals(tagName.toLowerCase()))) {
                newTags.add(new JobListingTag(tagName.toLowerCase(), tagName));
            }
        }

        if (!newTags.isEmpty()) {
            this.jobListingTagRepository.saveAll(newTags);
            tags.addAll(newTags);
        }

        return new HashSet<>(tags);
    }

}
