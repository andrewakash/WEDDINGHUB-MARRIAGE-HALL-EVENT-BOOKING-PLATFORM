package com.weddinghub.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.weddinghub.repository.UserRepository;

@Service
public class WeddingHubUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    public WeddingHubUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return users.findByEmailIgnoreCase(email.trim())
                .map(WeddingHubPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password."));
    }
}
