package br.org.catolicasc.pug.identity.service;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.LoginRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.TokenResponse;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import java.util.UUID;

/**
 * Application service interface for handling authentication and authorization flows.
 *
 * <p>This service orchestrates credential verification and the generation of secure JSON Web Tokens
 * (JWT) for authenticated accounts. It also exposes helper methods to resolve the current
 * authenticated principal from the security context.
 */
public interface AuthService {

  /**
   * Resolves the current authenticated account identifier from the active security context.
   *
   * <p>Implementations must throw a {@link jakarta.ws.rs.NotAuthorizedException} when there is no
   * authenticated principal or the JWT does not contain the {@code accountId} claim.
   *
   * @return the UUID of the authenticated account
   */
  UUID getCurrentAccountId();

  /**
   * Retrieves the {@link AccountType} of the currently authenticated account based on the JWT
   * {@code groups} claim.
   *
   * @return the {@link AccountType} of the current account
   * @throws jakarta.ws.rs.NotAuthorizedException if there is no authenticated principal or the
   *     token does not contain a valid group
   */
  AccountType getCurrentAccountType();

  /**
   * Resolves the current authenticated user identifier from the active security context.
   *
   * <p>Implementations must throw a {@link jakarta.ws.rs.NotAuthorizedException} when there is no
   * authenticated principal or the JWT does not contain the {@code userId} claim.
   *
   * @return the UUID of the authenticated user
   */
  UUID getCurrentUserId();

  /**
   * Authenticates a user based on their credentials and generates a JSON Web Token (JWT).
   *
   * <p>This method evaluates the provided email and plaintext password. If the credentials are
   * valid and the underlying {@link Account} is active, it issues a signed JWT containing the
   * user's roles and identity claims.
   *
   * @param request the structured {@link LoginRequest} containing the user's email and plaintext
   *     password
   * @return a {@link TokenResponse} containing the generated JWT, account identifier, role, and
   *     lifespan
   * @throws jakarta.ws.rs.NotAuthorizedException if the account cannot be found, the password does
   *     not match, or the account is currently marked as inactive
   */
  TokenResponse login(LoginRequest request);

  /**
   * Ensures that the currently authenticated account is not of the given forbidden type.
   *
   * @param forbidden the {@link AccountType} that is not allowed to perform the current operation
   * @throws jakarta.ws.rs.NotAuthorizedException if the current account type matches the forbidden
   *     type, or if there is no authenticated principal
   */
  void requireCurrentAccountNotOfType(AccountType forbidden);

  /**
   * Ensures that the currently authenticated account is of the given allowed type.
   *
   * @param allowed the {@link AccountType} that is allowed to perform the current operation
   * @throws jakarta.ws.rs.NotAuthorizedException if the current account type do not matches the
   *     allowed type, or if there is no authenticated principal
   */
  void requireCurrentAccountOfType(AccountType allowed);
}
