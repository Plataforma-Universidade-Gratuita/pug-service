package com.pug.geo.presenter.dtos;

import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for partially updating an existing
 * City.
 *
 * <p>Because updates can be partial, all fields in this record are inherently optional. If a field
 * is provided as {@code null} or omitted from the JSON payload, the application service will ignore
 * it and retain the existing value for that specific attribute.
 *
 * @param name the new name to assign to the city, or {@code null} to leave unchanged (if provided,
 *     must be max 100 characters)
 * @param ibgeCodeString the new 7-digit IBGE code string, or {@code null} to leave unchanged
 */
public record CityUpdateRequest(@Size(max = 100) String name, String ibgeCodeString) {}
