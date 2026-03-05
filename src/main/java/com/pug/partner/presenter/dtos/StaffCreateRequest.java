package com.pug.partner.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for assigning a new Staff member.
 *
 * <p>This record acts as an aggregated payload that combines identity credentials with
 * organizational assignment. It applies Jakarta Bean Validation to ensure all required components
 * are present before attempting a complex, multi-domain transaction.
 *
 * @param cpfString the raw 11-digit numeric CPF string of the person (must not be blank)
 * @param name the full name of the staff member (must not be blank and max 150 characters)
 * @param emailString the email address for the staff member's account (must not be blank)
 * @param password the requested password for authentication (must not be blank, between 8 and 255
 *     characters)
 * @param entityId the unique identifier (UUID) of the partner entity they will manage (must not be
 *     null)
 */
public record StaffCreateRequest(
    @NotBlank String cpfString,
    @NotBlank @Size(max = 150) String name,
    @NotBlank String emailString,
    @NotBlank @Size(min = 8, max = 255) String password,
    @NotNull UUID entityId) {}
