package com.pug.partner.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO for creating an Entity.
 *
 * @param cnpjString the CNPJ of the entity as a string.
 * @param name the name of the entity.
 * @param cityId the ID of the city associated with the entity, as a UUID.
 * @param address the address of the entity.
 */
public record EntityCreateRequest(
    @NotBlank String cnpjString,
    @NotBlank @Size(max = 150) String name,
    @NotBlank UUID cityId,
    @Size(max = 254) String address) {}
