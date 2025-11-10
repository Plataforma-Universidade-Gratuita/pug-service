package com.pug.partner.presenter.dtos;

import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/** Create/Update payload for Entity. */
public record EntityCreateOrUpdateRequest(
    @NotBlank String cnpj,
    @NotBlank @Size(max = 150) String name,
    @NotNull @UuidV7 UUID cityId,
    @Size(max = 254) String address) {}
