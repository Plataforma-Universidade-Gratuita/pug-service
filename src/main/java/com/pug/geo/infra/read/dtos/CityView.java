package com.pug.geo.infra.read.dtos;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only view of a City.
 * <p>
 * Following CQRS principles, this record is used exclusively for returning queried data
 * to the client. It flattens the internal domain structure (e.g., extracting the raw string
 * from the {@code IbgeCode} value object) to provide a simple, lightweight structure
 * optimized for JSON serialization.
 *
 * @param id       the unique identifier (UUIDv7) of the city
 * @param name     the name of the city
 * @param ibgeCode the 7-digit Brazilian Institute of Geography and Statistics code
 */
public record CityView(UUID id, String name, String ibgeCode) {
}