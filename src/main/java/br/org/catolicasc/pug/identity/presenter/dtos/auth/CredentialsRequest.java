package br.org.catolicasc.pug.identity.presenter.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) representing the credential-wiring payload for an account.
 *
 * @param email the target account email whose credentials should be wired
 * @param password the raw password to validate and hash before persistence
 */
public record CredentialsRequest(
    @NotBlank @Email @Size(max = 254) String email, @NotBlank @Size(min = 8, max = 255) String password) {}
