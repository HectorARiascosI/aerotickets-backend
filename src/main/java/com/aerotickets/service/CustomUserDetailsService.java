package com.aerotickets.service;

import com.aerotickets.constants.SecurityMessagesConstants;
import com.aerotickets.entity.User;
import com.aerotickets.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = users.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                SecurityMessagesConstants.ERR_USER_NOT_FOUND_PREFIX + email
                        )
                );

        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPasswordHash(),
                u.isEnabled(),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole()))
        );
    }
}