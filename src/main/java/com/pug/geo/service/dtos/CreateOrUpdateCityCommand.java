package com.pug.geo.service.dtos;

import com.pug.geo.domain.vos.IbgeCode;

/**
 * Command to create a new city.
 *
 * @param name the name of the city
 * @param ibgeCode the IBGE code of the city
 */
public record CreateOrUpdateCityCommand(String name, IbgeCode ibgeCode) {}
