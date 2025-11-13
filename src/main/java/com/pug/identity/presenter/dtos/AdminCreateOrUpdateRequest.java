package com.pug.identity.presenter.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

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
    @NotBlank String name,
    @NotBlank @Email String email,
    @NotBlank String password) {}
