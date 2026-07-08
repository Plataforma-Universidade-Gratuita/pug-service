package br.org.catolicasc.pug.identity.presenter.dtos.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for logging out (revoking a refresh token).
 *
 * @param refreshToken the opaque refresh token to revoke
 */
public record LogoutRequest(@NotBlank String refreshToken) {}
