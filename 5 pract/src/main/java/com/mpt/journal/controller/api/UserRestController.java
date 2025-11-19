package com.mpt.journal.controller.api;

import com.mpt.journal.dto.api.UserCreateRequest;
import com.mpt.journal.dto.api.UserResponseDto;
import com.mpt.journal.dto.api.UserUpdateRequest;
import com.mpt.journal.entity.User;
import com.mpt.journal.exception.ResourceNotFoundException;
import com.mpt.journal.repository.UserRepository;
import com.mpt.journal.service.UserAsyncService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAsyncService userAsyncService;

    public UserRestController(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              UserAsyncService userAsyncService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAsyncService = userAsyncService;
    }

    @GetMapping
    public List<UserResponseDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/async")
    public CompletableFuture<List<UserResponseDto>> getAllAsync() {
        return userAsyncService.findAllAsync()
                .thenApply(users -> users.stream()
                        .map(this::toDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User with id " + id + " not found"));
        return toDto(user);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(request.isEnabled());

        User saved = userRepository.save(user);

        URI location = URI.create("/api/users/" + saved.getId());
        return ResponseEntity.created(location).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public UserResponseDto update(@PathVariable Long id,
                                  @Valid @RequestBody UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User with id " + id + " not found"));

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        User updated = userRepository.save(user);
        return toDto(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.isEnabled()
        );
    }
}