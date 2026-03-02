package com.pug.geo.service.dtos;

/**
 * Data Transfer Object (DTO) acting as an application command to update an existing geographic City.
 * <p>
 * This record encapsulates the requested state changes for a city. The fields are typically
 * treated as optional during the update process—meaning if a field is omitted (null or empty),
 * the application service will retain the existing value for that specific attribute.
 *
 * @param name           the new name to assign to the city, or {@code null} to leave unchanged
 * @param ibgeCodeString the new raw 7-digit IBGE code string, or {@code null} to leave unchanged
 */
public record CityUpdateCommand(String name, String ibgeCodeString) {
}