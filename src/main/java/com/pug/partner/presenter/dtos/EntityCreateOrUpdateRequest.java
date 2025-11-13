package com.pug.partner.presenter.dtos;

import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request representation for creating or updating an Entity.
 *
 * @param cnpj    the CNPJ of the entity
 * @param name    the name of the entity
 * @param cityId  the unique identifier of the city associated with the entity
 * @param address the address of the entity
 */
public record EntityCreateOrUpdateRequest(
        @NotBlank String cnpj,
        @NotBlank @Size(max = 150) String name,
        @NotNull @UuidV7 UUID cityId,
        @Size(max = 254) String address) {
}
