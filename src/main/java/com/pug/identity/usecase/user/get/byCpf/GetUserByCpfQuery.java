package com.pug.identity.usecase.user.get.byCpf;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record GetUserByCpfQuery(@NotBlank @CPF String cpf) {}
