package com.pug.partner.service.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdatePartnerEntityCommand(
    @NotNull UUID id,
    @NotBlank String cnpj,
    @NotBlank @Size(max = 150) String name,
    @NotNull UUID cityId,
    @Size(max = 254) String address,
    Boolean active) {}
