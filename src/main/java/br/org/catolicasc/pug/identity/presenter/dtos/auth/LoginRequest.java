package br.org.catolicasc.pug.identity.presenter.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) representing the client's credentials for authentication.
 *
 * @param email the user's registered email address
 * @param password the raw, plaintext password
 */
public record LoginRequest(
    @NotBlank @Email @Size(max = 254) String email, @NotBlank @Size(max = 255) String password) {}
