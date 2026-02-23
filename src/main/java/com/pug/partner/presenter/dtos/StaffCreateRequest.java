package com.pug.partner.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Request to create a new staff member.
 *
 * @param cpfString the CPF of the staff member as a string.
 * @param name the name of the staff member.
 * @param emailString the email of the staff member as a string.
 * @param password the password for the staff member's account.
 * @param entityId the CNPJ of the entityId the staff is part of, as a UUID.
 */
public record StaffCreateRequest(
    @NotBlank String cpfString,
    @NotBlank @Size(max = 150) String name,
    @NotBlank String emailString,
    @NotBlank @Size(min = 8, max = 255) String password,
    @NotBlank UUID entityId) {}
