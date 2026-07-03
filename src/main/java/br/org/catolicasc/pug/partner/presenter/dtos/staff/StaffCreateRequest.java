/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.presenter.dtos.staff;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for assigning a new Staff member.
 *
 * <p>This record acts as an aggregated payload that combines identity details with organizational
 * assignment. Password setup is intentionally deferred, so the underlying account starts without a
 * stored password hash.
 *
 * @param cpf the raw 11-digit numeric CPF string of the person (must not be blank)
 * @param name the full name of the staff member (must not be blank and max 150 characters)
 * @param email the email address for the staff member's account (must not be blank)
 * @param entityId the unique identifier (UUID) of the partner entity they will manage (must not be
 *     null)
 */
public record StaffCreateRequest(
    @NotBlank @JsonProperty("cpf") String cpf,
    @NotBlank @Size(max = 150) String name,
    @NotBlank @JsonProperty("email") String email,
    @NotNull UUID entityId) {}
