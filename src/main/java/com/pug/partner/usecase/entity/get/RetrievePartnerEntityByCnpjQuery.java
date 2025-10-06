package com.pug.partner.usecase.entity.get;

import jakarta.validation.constraints.NotBlank;

public record RetrievePartnerEntityByCnpjQuery(@NotBlank String cnpj) {}
