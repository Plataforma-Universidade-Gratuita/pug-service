package br.org.catolicasc.pug.geo.presenter.dtos;

import jakarta.validation.constraints.Size;

/**
 * Request DTO used in paginated complex-search operations for cities.
 *
 * @param name the city name filter. Must not exceed 100 characters.
 */
public record CityComplexSearchRequest(@Size(max = 100) String name) {}
