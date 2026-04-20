package br.org.catolicasc.pug.identity.presenter.dtos.auth;

import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) containing the generated JSON Web Token and user context.
 *
 * <p>This record is returned to the client upon successful authentication, providing the bearer
 * token needed for subsequent API requests, along with basic context to update the UI state.
 *
 * @param token the encoded JWT bearer token
 * @param accountId the unique identifier of the authenticated account
 * @param accountType the authorization role of the account
 * @param expiresIn the number of seconds until the token expires
 */
public record TokenResponse(
    String token, UUID accountId, AccountType accountType, long expiresIn) {}
