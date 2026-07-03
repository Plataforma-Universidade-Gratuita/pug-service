/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

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
    @NotBlank @Email @Size(max = 254) String email, @Size(min = 8, max = 255) String password) {}
