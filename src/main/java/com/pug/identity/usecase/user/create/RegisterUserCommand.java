package com.pug.identity.usecase.user.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record RegisterUserCommand(
    @NotBlank @CPF String cpf, @NotBlank @Size(max = 150) String name) {}
