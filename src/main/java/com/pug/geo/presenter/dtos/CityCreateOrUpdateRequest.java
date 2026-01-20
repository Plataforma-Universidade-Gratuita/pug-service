package com.pug.geo.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating a city.
 *
 * @param name           the name of the city
 * @param ibgeCodeString the IBGE code of the city as a string
 */
public record CityCreateOrUpdateRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        String ibgeCodeString) {
}