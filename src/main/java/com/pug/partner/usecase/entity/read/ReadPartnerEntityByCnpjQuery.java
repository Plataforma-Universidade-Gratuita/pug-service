package com.pug.partner.usecase.entity.read;

import jakarta.validation.constraints.NotBlank;

public record ReadPartnerEntityByCnpjQuery(@NotBlank String cnpj) {}
