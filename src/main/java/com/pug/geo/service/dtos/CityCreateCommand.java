package com.pug.geo.service.dtos;

/**
 * Data Transfer Object (DTO) acting as an application command to create a new geographic City.
 *
 * <p>This record encapsulates the raw input data required by the application service to instantiate
 * a new {@link com.pug.geo.domain.City} aggregate.
 *
 * @param name the requested name for the new city
 * @param ibgeCodeString the raw 7-digit string representing the IBGE code
 */
public record CityCreateCommand(String name, String ibgeCodeString) {}
