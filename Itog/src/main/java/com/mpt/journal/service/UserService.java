package com.mpt.journal.service;

import com.mpt.journal.dto.UserRegistrationDto;
import com.mpt.journal.entity.User;

public interface UserService {

    boolean usernameExists(String username);

    User register(UserRegistrationDto dto);
}
