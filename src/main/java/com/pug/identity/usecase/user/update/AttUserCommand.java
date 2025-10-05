package com.pug.identity.usecase.user.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import org.hibernate.validator.constraints.br.CPF;

public record AttUserCommand(
    @NotNull UUID id, @NotBlank @CPF String cpf, @NotBlank @Size(max = 150) String name) {}
