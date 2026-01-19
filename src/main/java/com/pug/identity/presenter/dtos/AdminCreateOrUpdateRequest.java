package com.pug.identity.presenter.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * AdminCreateOrUpdateRequest record.
 *
 * @param cpf the CPF number
 * @param name the name of the user
 * @param email the email of the user
 * @param password the password of the user
 */
public record AdminCreateOrUpdateRequest(
    @NotBlank String cpf,
    @NotBlank @Size(max = 150) String name,
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Size(min = 8, max = 255) String password) {}
