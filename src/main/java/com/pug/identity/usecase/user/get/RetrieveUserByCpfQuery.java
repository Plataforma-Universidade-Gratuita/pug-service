package com.pug.identity.usecase.user.get;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record RetrieveUserByCpfQuery(@NotBlank @CPF String cpf) {}
