package com.weddinghub.config;

import java.util.Locale;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.weddinghub.entity.User;
import com.weddinghub.entity.UserRole;
import com.weddinghub.repository.UserRepository;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String fullName;
    private final String password;

    public AdminBootstrap(UserRepository users, PasswordEncoder passwordEncoder,
                          @Value("${WEDDINGHUB_ADMIN_EMAIL:}") String email,
                          @Value("${WEDDINGHUB_ADMIN_FULL_NAME:WeddingHub Admin}") String fullName,
                          @Value("${WEDDINGHUB_ADMIN_PASSWORD:}") String password) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (email.isBlank() && password.isBlank()) return;
        int passwordBytes = password.getBytes(StandardCharsets.UTF_8).length;
        if (email.isBlank() || fullName.isBlank() || passwordBytes < 12 || passwordBytes > 72) {
            throw new IllegalStateException("Set WEDDINGHUB_ADMIN_EMAIL and a 12–72 byte UTF-8 WEDDINGHUB_ADMIN_PASSWORD to bootstrap the admin account.");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        users.findByEmailIgnoreCase(normalizedEmail).ifPresentOrElse(user -> {
            if (user.getRole() != UserRole.ADMIN) {
                throw new IllegalStateException("The configured admin email is already registered with a different role.");
            }
        }, () -> users.save(new User(fullName.trim(), normalizedEmail,
                passwordEncoder.encode(password), null, UserRole.ADMIN)));
    }
}
