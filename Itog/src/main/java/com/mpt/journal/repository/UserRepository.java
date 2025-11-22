package com.mpt.journal.repository;

import com.mpt.journal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username) ;
    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);

    Optional<User> findByUsername(String username);

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Page<User> findAll(Pageable pageable);
}
