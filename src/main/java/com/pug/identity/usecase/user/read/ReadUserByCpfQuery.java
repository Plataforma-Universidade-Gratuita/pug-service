package com.pug.identity.usecase.user.read;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record ReadUserByCpfQuery(@NotBlank @CPF String cpf) {}
