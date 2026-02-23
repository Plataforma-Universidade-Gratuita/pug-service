package com.pug.partner.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Request DTO for creating an Entity.
 *
 * @param cnpjString the CNPJ of the entityId as a string.
 * @param name the name of the entityId.
 * @param cityId the ID of the city associated with the entityId, as a UUID.
 * @param address the address of the entityId.
 */
public record EntityCreateRequest(
    @NotBlank String cnpjString,
    @NotBlank @Size(max = 150) String name,
    @NotBlank UUID cityId,
    @Size(max = 254) String address) {}
