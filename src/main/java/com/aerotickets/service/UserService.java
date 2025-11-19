package com.aerotickets.service;

import com.aerotickets.constants.UserServiceConstants;
import com.aerotickets.entity.User;
import com.aerotickets.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String fullName, String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    UserServiceConstants.ERR_EMAIL_ALREADY_REGISTERED
            );
        }

        User user = User.builder()
                .fullName(fullName)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .build();

        return userRepository.save(user);
    }
}