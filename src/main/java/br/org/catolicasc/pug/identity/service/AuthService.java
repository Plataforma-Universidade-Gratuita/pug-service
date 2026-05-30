package br.org.catolicasc.pug.identity.service;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.CredentialsRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.LoginRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.LogoutRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.RefreshRequest;
import br.org.catolicasc.pug.identity.presenter.dtos.auth.TokenResponse;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import java.util.UUID;

/**
 * Application service interface for handling authentication and authorization flows.
 *
 * <p>This service orchestrates credential verification, the generation of secure JSON Web Tokens
 * (JWT) as short-lived access tokens, and the management of long-lived refresh tokens for session
 * continuity. It also exposes helper methods to resolve the current authenticated principal from
 * the security context.
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
   * Authenticates a user based on their credentials and generates an access/refresh token pair.
   *
   * <p>This method evaluates the provided email and plaintext password. If the credentials are
   * valid and the underlying {@link Account} is active, it issues a signed short-lived JWT access
   * token and a long-lived opaque refresh token persisted in the database. Accounts provisioned
   * without a password hash are still allowed to authenticate so they can complete the
   * credential-wiring flow, but their issued token is flagged to restrict protected operations
   * until a password is set.
   *
   * @param request the structured {@link LoginRequest} containing the user's email and plaintext
   *     password
   * @return a {@link TokenResponse} containing the access token, refresh token, account identifier,
   *     role, and lifespans
   * @throws jakarta.ws.rs.NotAuthorizedException if the account cannot be found, the password does
   *     not match, or the account is currently marked as inactive
   */
  TokenResponse login(LoginRequest request);

  /**
   * Revokes a refresh token, effectively logging the user out.
   *
   * <p>After this operation, the refresh token can no longer be used to obtain new access tokens.
   * Any existing access tokens will expire naturally.
   *
   * @param request the structured {@link LogoutRequest} containing the refresh token to revoke
   */
  void logout(LogoutRequest request);

  /**
   * Revokes all refresh tokens for the currently authenticated account, logging out from all
   * devices/sessions.
   *
   * @throws jakarta.ws.rs.NotAuthorizedException if there is no authenticated principal
   */
  void logoutAll();

  /**
   * Validates a refresh token and issues a new short-lived access token.
   *
   * <p>The refresh token itself remains unchanged until it expires or is explicitly revoked.
   *
   * @param request the structured {@link RefreshRequest} containing the opaque refresh token
   * @return a {@link TokenResponse} with a fresh access token and the same refresh token
   * @throws jakarta.ws.rs.NotAuthorizedException if the refresh token is invalid, expired, or the
   *     associated account is inactive
   */
  TokenResponse refresh(RefreshRequest request);

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
   * @throws jakarta.ws.rs.NotAuthorizedException if the current account type does not match the
   *     allowed type, or if there is no authenticated principal
   */
  void requireCurrentAccountOfType(AccountType allowed);

  /**
   * Wires the first password, or replaces the current password, for the account identified by the
   * supplied email address.
   *
   * <p>This operation centralizes the password onboarding flow for accounts that were provisioned
   * without credentials during admin, partner, or formerStudent creation workflows.
   *
   * @param request the structured {@link CredentialsRequest} containing the target email and the
   *     desired raw password
   * @throws br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException if the account does
   *     not exist
   * @throws br.org.catolicasc.pug.shared.exceptions.BusinessRuleException if the proposed password
   *     does not satisfy the platform's strength policy
   */
  void wireCredentials(CredentialsRequest request);
}
