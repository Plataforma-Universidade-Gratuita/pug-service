package com.pug.partner.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating an Entity.
 *
 * @param cnpjString     the CNPJ of the entity as a string.
 * @param name           the name of the entity.
 * @param cityIbgeString the IBGE code of the city associated with the entity, as a string.
 * @param address        the address of the entity.
 */
public record EntityCreateRequest(
        @NotBlank String cnpjString,
        @NotBlank @Size(max = 150) String name,
        @NotBlank String cityIbgeString,
        @Size(max = 254) String address) {
}
