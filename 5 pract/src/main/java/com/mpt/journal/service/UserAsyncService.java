package com.mpt.journal.service;

import com.mpt.journal.entity.User;
import com.mpt.journal.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserAsyncService {

    private final UserRepository userRepository;

    public UserAsyncService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Async
    public CompletableFuture<List<User>> findAllAsync() {
        List<User> users = userRepository.findAll();
        // тут мог быть тяжёлый запрос или внешняя система
        return CompletableFuture.completedFuture(users);
    }
}