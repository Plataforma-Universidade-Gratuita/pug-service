package com.pug.identity.presenter.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Request payload for POST /admins. */
public record AdminCreateRequest(
    @NotBlank String cpf,
    @NotBlank String name,
    @NotBlank @Email String email,
    @NotBlank String password) {}
