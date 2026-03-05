package com.pug.geo.presenter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for creating a new City.
 *
 * <p>This record applies Jakarta Bean Validation constraints to ensure the initial data is
 * structurally sound before it reaches the application service layer.
 *
 * @param name the requested name of the city (must not be blank and max 100 characters)
 * @param ibgeCodeString the exact 7-digit IBGE code as a string (must not be blank)
 */
public record CityCreateRequest(
    @NotBlank @Size(max = 100) String name, @NotBlank String ibgeCodeString) {}
