package com.pug.partner.usecase.entity.update;

import jakarta.validation.constraints.Size;
import java.util.UUID;
import org.hibernate.validator.constraints.br.CNPJ;

/** Partial update. Only non-null fields are applied. */
public record UpdatePartnerEntityCommand(
    UUID id,
    @CNPJ String cnpj,
    @Size(max = 150) String name,
    UUID cityId,
    @Size(max = 254) String address) {}
