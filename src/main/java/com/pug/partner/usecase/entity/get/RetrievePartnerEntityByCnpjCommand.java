package com.pug.partner.usecase.entity.get;

import jakarta.validation.constraints.NotBlank;

public record RetrievePartnerEntityByCnpjCommand(@NotBlank String cnpj) {}
