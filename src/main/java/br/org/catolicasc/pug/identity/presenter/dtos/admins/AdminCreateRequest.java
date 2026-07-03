/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.presenter.dtos.admins;

import br.org.catolicasc.pug.shared.domain.enums.Campi;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for creating a new Administrator.
 *
 * <p>This record applies Jakarta Bean Validation constraints to ensure the initial data is
 * structurally sound before it reaches the application service layer. It acts as an aggregate
 * payload that will eventually be split to create the User, Account, and Admin records.
 *
 * @param cpf the raw 11-digit CPF string (must not be blank)
 * @param name the full name of the administrator (must not be blank and max 150 characters)
 * @param email the email address for the administrator's account (must not be blank)
 * @param campus the designated university campus enum (must not be null)
 */
public record AdminCreateRequest(
    @NotBlank @JsonProperty("cpf") String cpf,
    @NotBlank @Size(max = 150) String name,
    @NotBlank @JsonProperty("email") String email,
    @NotNull Campi campus) {}
