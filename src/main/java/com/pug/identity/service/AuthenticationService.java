package com.pug.identity.service;

import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;
import java.util.UUID;

/**
 * Authenticates users by email and password against stored bcrypt hashes.
 */
@ApplicationScoped
public class AuthenticationService {

    @Inject
    UsersService usersService;
    @Inject
    PasswordService passwords;

    /**
     * Verify credentials. Returns identity on success, otherwise invalid.
     */
    public AuthResult authenticate(String emailRaw, String passwordRaw) {
        if (emailRaw == null || passwordRaw == null || passwordRaw.isBlank()) return AuthResult.invalid();

        // canonicalize + validate email using VO rules
        final String email = new Email(emailRaw).toString();

        final User u;
        try {
            u = usersService.getByEmail(email); // throws if not found
        } catch (ResourceNotFoundException e) {
            return AuthResult.invalid();
        }

        String stored = u.getPasswordHash();
        if (stored == null || stored.isBlank()) return AuthResult.invalid();

        boolean ok = passwords.verify(stored, passwordRaw);
        return ok ? AuthResult.valid(u) : AuthResult.invalid();
    }

    /**
     * Same as authenticate but returns the full User on success.
     */
    public Optional<User> authenticateAndGet(String emailRaw, String passwordRaw) {
        AuthResult r = authenticate(emailRaw, passwordRaw);
        if (!r.valid) return Optional.empty();
        try {
            return Optional.of(usersService.getByEmail(new Email(emailRaw).toString()));
        } catch (ResourceNotFoundException e) {
            return Optional.empty();
        }
    }

    /**
     * Lightweight auth result.
     */
    public record AuthResult(boolean valid, UUID userId, AccountType accountType) {
        static AuthResult valid(User u) {
            return new AuthResult(true, u.getId(), u.getAccountType());
        }

        static AuthResult invalid() {
            return new AuthResult(false, null, null);
        }
    }
}
