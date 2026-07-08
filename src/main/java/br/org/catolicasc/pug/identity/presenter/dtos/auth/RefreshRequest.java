package br.org.catolicasc.pug.identity.presenter.dtos.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for refreshing an access token.
 *
 * @param refreshToken the opaque refresh token previously issued during login
 */
public record RefreshRequest(@NotBlank String refreshToken) {}
