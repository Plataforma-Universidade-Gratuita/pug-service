package com.pug.geo.presenter.dtos;

import java.util.UUID;

/**
 * Response DTO for a city.
 *
 * @param id       the unique identifier of the city
 * @param name     the name of the city
 * @param ibgeCode the IBGE code of the city
 */
public record CityResponse(UUID id, String name, String ibgeCode) {
}
