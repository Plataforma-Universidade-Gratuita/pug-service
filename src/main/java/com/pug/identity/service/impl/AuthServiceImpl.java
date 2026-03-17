package com.pug.identity.service.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.AccountRepository;
import com.pug.identity.presenter.dtos.auth.LoginRequest;
import com.pug.identity.presenter.dtos.auth.TokenResponse;
import com.pug.identity.service.AuthService;
import com.pug.identity.service.PasswordService;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Set;

/**
 * Implementation of the {@link AuthService} utilizing SmallRye JWT.
 *
 * <p>This application-scoped bean verifies credentials against the database and signs a new JWT
 * containing the user's role and identifiers as claims.
 */
@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    private static final Logger LOG = Logger.getLogger(AuthServiceImpl.class);

    @Inject
    AccountRepository accountRepository;

    @Inject
    PasswordService passwordService;

    @ConfigProperty(name = "smallrye.jwt.new-token.lifespan", defaultValue = "28800")
    long lifespan;

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenResponse login(LoginRequest request) {
        LOG.debugf("Attempting authentication for email: %s", request.email());

        // Buscamos a conta. Usamos um NotAuthorizedException genérico por segurança
        // para não revelar se o erro foi no email ou na senha.
        Account account =
                accountRepository
                        .find("email", request.email())
                        .firstResultOptional()
                        .orElseThrow(() -> new NotAuthorizedException("Invalid credentials"));

        if (Boolean.FALSE.equals(account.getActive())) {
            LOG.warnf("Authentication failed: Account %s is deactivated", account.getId());
            throw new NotAuthorizedException("Account is inactive");
        }

        if (!passwordService.verify(account.getPasswordHash(), request.password())) {
            LOG.warnf("Authentication failed: Invalid password for account %s", account.getId());
            throw new NotAuthorizedException("Invalid credentials");
        }

        String token =
                Jwt.upn(account.getEmail().getValue())
                        .groups(Set.of(account.getAccountType().name()))
                        .claim("accountId", account.getId().toString())
                        .claim("userId", account.getUserId().toString())
                        .sign();

        LOG.infof("Authentication successful for account %s", account.getId());
        return new TokenResponse(token, account.getId(), account.getAccountType(), lifespan);
    }
}