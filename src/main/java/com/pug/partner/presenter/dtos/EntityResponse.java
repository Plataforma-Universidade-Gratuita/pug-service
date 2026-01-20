package com.pug.partner.presenter.dtos;

import com.pug.geo.presenter.dtos.CityResponse;

import java.util.UUID;

/**
 * Response DTO for an Entity.
 *
 * @param id            the unique identifier of the entity
 * @param cnpj          the CNPJ of the entity
 * @param cnpjFormatted the formatted CNPJ of the entity (XX.XXX.XXX/XXXX-XX)
 * @param name          the name of the entity
 * @param address       the address of the entity
 * @param city          the city response associated with the entity
 */
public record EntityResponse(
        UUID id, String cnpj, String cnpjFormatted, String name, String address, CityResponse city) {
}