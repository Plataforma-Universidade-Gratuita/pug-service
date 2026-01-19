package com.pug.partner.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request representation for creating or updating an Entity.
 *
 * @param cnpj the CNPJ of the entity
 * @param name the name of the entity
 * @param cityIbge the IBGE code of the city associated with the entity
 * @param address the address of the entity
 */
public record EntityCreateRequest(
    @NotBlank String cnpj,
    @NotBlank @Size(max = 150) String name,
    @NotNull String cityIbge,
    @Size(max = 254) String address) {}
