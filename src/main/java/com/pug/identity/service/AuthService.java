package com.pug.identity.service;

import com.pug.identity.presenter.dtos.auth.LoginRequest;
import com.pug.identity.presenter.dtos.auth.TokenResponse;

/**
 * Application service interface for handling authentication and authorization flows.
 *
 * <p>This service orchestrates credential verification and the generation of secure JSON Web Tokens
 * (JWT) for authenticated accounts.
 */
public interface AuthService {

    /**
     * Authenticates a user based on their credentials and generates a JWT.
     *
     * @param request the structured command containing the email and plaintext password
     * @return a {@link TokenResponse} containing the generated JWT and account metadata
     * @throws jakarta.ws.rs.NotAuthorizedException if the credentials do not match or the account is
     *                                              inactive
     */
    TokenResponse login(LoginRequest request);
}