package com.pug.partner.service.dtos;

import java.util.UUID;

/**
 * Command DTO for updating an Entity.
 *
 * @param cnpjString the CNPJ of the entity as a string (optional).
 * @param name       the name of the entity (optional).
 * @param cityId     the ID of the city where the entity is located (optional).
 * @param address    the address where the entity is located (optional).
 */
public record EntityUpdateCommand(
        String cnpjString,
        String name,
        UUID cityId,
        String address) {
}