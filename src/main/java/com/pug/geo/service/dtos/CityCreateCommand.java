package com.pug.geo.service.dtos;

/**
 * Command for creating a city.
 *
 * @param name the name of the city.
 * @param ibgeCodeString the IBGE code of the city as a string.
 */
public record CityCreateCommand(String name, String ibgeCodeString) {}
