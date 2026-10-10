package com.carmod.app.service;

import com.carmod.app.dto.UserProfileDto;
import com.carmod.app.dto.UserRegistrationDto;
import com.carmod.app.entity.User;
import com.carmod.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public User registerCustomer(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        if (!dto.isPasswordMatching()) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setRole(User.Role.CUSTOMER);
        user.setStatus(User.AccountStatus.ACTIVE);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User updateProfile(Long userId, UserProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check email uniqueness if changed
        if (!user.getEmail().equalsIgnoreCase(dto.getEmail()) &&
            userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use by another account.");
        }

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());

        if (dto.isPasswordChangeRequested()) {
            if (!dto.isPasswordMatching()) {
                throw new IllegalArgumentException("New passwords do not match.");
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<User> getAllCustomers(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return userRepository.searchCustomers(search.trim(), pageable);
        }
        return userRepository.findByRole(User.Role.CUSTOMER, pageable);
    }

    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getStatus() == User.AccountStatus.ACTIVE) {
            user.setStatus(User.AccountStatus.SUSPENDED);
        } else {
            user.setStatus(User.AccountStatus.ACTIVE);
        }
        userRepository.save(user);
    }

    public UserProfileDto toProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        return dto;
    }

    @Transactional(readOnly = true)
    public long countCustomers() {
        return userRepository.countCustomers();
    }
}
