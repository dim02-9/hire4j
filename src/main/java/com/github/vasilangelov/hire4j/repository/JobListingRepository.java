package com.github.vasilangelov.hire4j.repository;

import com.github.vasilangelov.hire4j.dto.JobListingDetailsView;
import com.github.vasilangelov.hire4j.dto.LocationWithCountView;
import com.github.vasilangelov.hire4j.dto.TagWithCountView;
import com.github.vasilangelov.hire4j.model.JobListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JobListingRepository extends CrudRepository<JobListing, Long> {

    Optional<JobListingDetailsView> findDetailsViewById(long id);

    @Query(" SELECT job " +
            "FROM JobListing job " +
            "WHERE " +
                "LOWER(job.title) LIKE CONCAT('%', LOWER(:search), '%') AND " +
                "(:tags IS NULL OR EXISTS (SELECT 1 FROM job.tags jt WHERE jt.normalizedName IN :tags)) AND " +
                "(:location IS NULL OR :location = '' OR (:location = 'remote' AND COALESCE(job.location, '') = '') OR (:location != 'remote' AND job.location = :location)) AND " +
                "(:years IS NULL OR :years = 0 OR job.minYearsOfExperience >= :years) " +
            "ORDER BY job.createdAt DESC ")
    Page<JobListingDetailsView> findPageBy(
            @Param("search") String search,
            @Param("tags") Collection<String> tags,
            @Param("location") String location,
            @Param("years") Byte minYearsOfExperience,
            Pageable pageable
    );

    @Query(" SELECT " +
                "job.location AS location, " +
                "COUNT(job.location) AS count " +
            "FROM JobListing job " +
            "WHERE " +
                "LOWER(job.title) LIKE CONCAT('%', LOWER(:search), '%') AND " +
                "(:tags IS NULL OR EXISTS (SELECT 1 FROM job.tags jt WHERE jt.normalizedName IN :tags)) AND " +
                "(:years IS NULL OR :years = 0 OR job.minYearsOfExperience >= :years) " +
            "GROUP BY job " +
            "ORDER BY count DESC, job.location ASC")
    List<LocationWithCountView> getJobListingLocationsBy(
            @Param("search") String search,
            @Param("tags") Collection<String> tags,
            @Param("years") Byte minYearsOfExperience
    );

    @Query(" SELECT " +
                "tag.displayName AS displayName, " +
                "tag.normalizedName AS normalizedName, " +
                "COUNT(tag) AS count " +
            "FROM JobListing job " +
            "JOIN job.tags tag " +
            "WHERE " +
                "LOWER(job.title) LIKE CONCAT('%', LOWER(:search), '%') AND " +
                "(:location IS NULL OR :location = '' OR (:location = 'remote' AND COALESCE(job.location, '') = '') OR (:location != 'remote' AND job.location = :location)) AND " +
                "(:years IS NULL OR :years = 0 OR job.minYearsOfExperience >= :years) " +
            "GROUP BY tag " +
            "ORDER BY count DESC, tag.normalizedName ASC")
    List<TagWithCountView> getJobListingTagsBy(
            @Param("search") String search,
            @Param("location") String location,
            @Param("years") Byte minYearsOfExperience
    );

    @Query("SELECT job " +
            "FROM JobListing job " +
            "ORDER BY job.createdAt DESC " +
            "LIMIT :count")
    Collection<JobListingDetailsView> findLatestJobListings(@Param("count") int count);

}
