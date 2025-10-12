package com.pug.identity.service.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserCommand(@NotBlank String cpf, @NotBlank @Size(max = 150) String name) {}
