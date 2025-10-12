package com.pug.partner.usecase.entity.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import org.hibernate.validator.constraints.br.CNPJ;

public record CreatePartnerEntityCommand(
    @CNPJ @NotBlank String cnpj,
    @NotBlank @Size(max = 150) String name,
    @NotNull UUID cityId,
    @Size(max = 254) String address) {}
