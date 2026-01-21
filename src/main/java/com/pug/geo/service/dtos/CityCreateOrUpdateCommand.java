package com.pug.geo.service.dtos;

/**
 * Command for creating or updating a city.
 *
 * @param name the name of the city.
 * @param ibgeCodeString the IBGE code of the city as a string.
 */
public record CityCreateOrUpdateCommand(String name, String ibgeCodeString) {}
