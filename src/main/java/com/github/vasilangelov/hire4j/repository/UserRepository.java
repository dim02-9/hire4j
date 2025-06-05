package com.github.vasilangelov.hire4j.repository;

import com.github.vasilangelov.hire4j.dto.UserView;
import com.github.vasilangelov.hire4j.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Collection<UserView> findBy();

    Page<UserView> findAllByEmailStartingWith(String email, Pageable pageable);

    @Transactional
    void deleteUserByEmail(String email);

}
