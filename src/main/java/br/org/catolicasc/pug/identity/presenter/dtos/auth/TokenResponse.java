/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.presenter.dtos.auth;

import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) containing the generated tokens and authenticated account context.
 *
 * <p>This record is returned to the client after a successful authentication or token refresh. It
 * provides the bearer access token for API requests, an opaque refresh token for obtaining new
 * access tokens, and account metadata required by the client to update its session state.
 *
 * @param token the encoded JWT bearer access token
 * @param refreshToken the opaque refresh token used to obtain new access tokens
 * @param accountId the unique identifier of the authenticated account
 * @param accountType the authorization role of the authenticated account
 * @param passwordWired whether the account already has credentials configured
 * @param expiresIn the number of seconds until the access token expires
 * @param refreshExpiresIn the number of seconds until the refresh token expires
 */
public record TokenResponse(
    String token,
    String refreshToken,
    UUID accountId,
    AccountType accountType,
    boolean passwordWired,
    long expiresIn,
    long refreshExpiresIn) {}
