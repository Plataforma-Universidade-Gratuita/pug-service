package com.pug.identity.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating an Admin.
 *
 * @param cpfString the CPF number as a string.
 * @param name the name of the user.
 * @param emailString the email of the user as a string.
 * @param password the password of the user.
 */
public record AdminCreateRequest(
    @NotBlank String cpfString,
    @NotBlank @Size(max = 150) String name,
    @NotBlank String emailString,
    @NotBlank @Size(min = 8, max = 255) String password) {}
