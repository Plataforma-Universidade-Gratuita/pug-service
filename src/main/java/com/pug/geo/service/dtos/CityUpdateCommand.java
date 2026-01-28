package com.pug.geo.service.dtos;

/**
 * Command for updating a city.
 *
 * @param name the name of the city.
 * @param ibgeCodeString the IBGE code of the city as a string.
 */
public record CityUpdateCommand(String name, String ibgeCodeString) {}
