package br.org.catolicasc.pug.identity.presenter.dtos.auth;

import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) containing the generated tokens and user context.
 *
 * <p>This record is returned to the client upon successful authentication, providing the bearer
 * access token for API requests, an opaque refresh token for obtaining new access tokens, and basic
 * context to update the UI state.
 *
 * @param token the encoded JWT bearer access token (short-lived)
 * @param refreshToken the opaque refresh token used to obtain new access tokens (long-lived)
 * @param accountId the unique identifier of the authenticated account
 * @param accountType the authorization role of the account
 * @param expiresIn the number of seconds until the access token expires
 * @param refreshExpiresIn the number of seconds until the refresh token expires
 */
public record TokenResponse(
    String token,
    String refreshToken,
    UUID accountId,
    AccountType accountType,
    long expiresIn,
    long refreshExpiresIn) {}
