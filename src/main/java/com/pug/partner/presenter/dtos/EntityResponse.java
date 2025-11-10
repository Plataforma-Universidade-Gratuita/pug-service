package com.pug.partner.presenter.dtos;

import com.pug.geo.presenter.dtos.CityResponse;
import java.util.UUID;

/**
 * Response representation of an Entity.
 *
 * @param id the unique identifier of the entity
 * @param cnpj the CNPJ of the entity
 * @param name the name of the entity
 * @param address the address of the entity
 * @param city the city response associated with the entity
 */
public record EntityResponse(
    UUID id, String cnpj, String name, String address, CityResponse city) {}
