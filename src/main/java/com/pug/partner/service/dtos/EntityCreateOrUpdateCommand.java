package com.pug.partner.service.dtos;

/**
 * Command DTO for creating or updating an Entity.
 *
 * @param cnpjString the CNPJ of the entity as a string.
 * @param name the name of the entity.
 * @param cityIbgeString the IBGE code of the city where the entity is located, as a string.
 * @param address the address where the entity is located.
 */
public record EntityCreateOrUpdateCommand(
    String cnpjString, String name, String cityIbgeString, String address) {}
