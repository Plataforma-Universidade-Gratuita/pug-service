package com.pug.partner.service.dtos;

import java.util.UUID;

/**
 * Command DTO for creating an Entity.
 *
 * @param cnpjString the CNPJ of the entity as a string.
 * @param name       the name of the entity.
 * @param cityId     the ID of the city where the entity is located.
 * @param address    the address where the entity is located.
 */
public record EntityCreateCommand(
        String cnpjString,
        String name,
        UUID cityId,
        String address) {
}