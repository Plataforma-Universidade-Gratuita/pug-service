package com.pug.identity.service;

import com.pug.identity.presenter.dtos.auth.LoginRequest;
import com.pug.identity.presenter.dtos.auth.TokenResponse;

/**
 * Application service interface for handling authentication and authorization flows.
 *
 * <p>This service orchestrates credential verification and the generation of secure JSON Web Tokens
 * (JWT) for authenticated accounts. It relies on underlying domain services to securely resolve
 * identity and validate hashed passwords, abstracting away direct persistence access.
 */
public interface AuthService {

  /**
   * Authenticates a user based on their credentials and generates a JSON Web Token (JWT).
   *
   * <p>This method evaluates the provided email and plaintext password. If the credentials are
   * valid and the underlying {@link com.pug.identity.domain.Account} is active, it issues a signed
   * JWT containing the user's roles and identity claims.
   *
   * @param request the structured {@link LoginRequest} containing the user's email and plaintext
   *     password
   * @return a {@link TokenResponse} containing the generated JWT, account identifier, role, and
   *     lifespan
   * @throws jakarta.ws.rs.NotAuthorizedException if the account cannot be found, the password does
   *     not match, or the account is currently marked as inactive
   */
  TokenResponse login(LoginRequest request);
}
