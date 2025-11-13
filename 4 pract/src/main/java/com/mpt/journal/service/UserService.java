package com.mpt.journal.service;

import com.mpt.journal.dto.UserRegistrationDto;
import com.mpt.journal.entity.Role;
import com.mpt.journal.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    boolean usernameExists(String username);

    User register(UserRegistrationDto dto);

    List<User> findAll();

    Optional<User> findById(Long id);

    User createUser(String username, String rawPassword, Role role, boolean enabled);

    User updateUser(Long id, String username, String rawPasswordOrNull, Role role, boolean enabled);

    void deleteUser(Long id);
}
