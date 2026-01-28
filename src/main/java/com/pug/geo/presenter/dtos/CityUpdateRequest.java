package com.pug.geo.presenter.dtos;

/**
 * Request DTO for creating or updating a city.
 *
 * @param name           the name of the city
 * @param ibgeCodeString the IBGE code of the city as a string
 */
public record CityUpdateRequest(
        String name, String ibgeCodeString) {
}
