package com.mpt.journal.service;

import com.mpt.journal.dto.UserRegistrationDto;
import com.mpt.journal.entity.Role;
import com.mpt.journal.entity.User;
import com.mpt.journal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User register(UserRegistrationDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);   // по умолчанию обычный пользователь
        user.setEnabled(true);
        return userRepository.save(user);
    }
}
