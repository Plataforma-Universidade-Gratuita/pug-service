package com.pug.geo.infra.read.dtos;

import java.util.UUID;

/**
 * City view DTO.
 *
 * @param id       the city ID
 * @param name     the city name
 * @param ibgeCode the IBGE code
 */
public record CityView(UUID id, String name, String ibgeCode) {
}
