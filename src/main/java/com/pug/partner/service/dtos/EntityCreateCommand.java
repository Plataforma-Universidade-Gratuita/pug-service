package com.pug.partner.service.dtos;

import java.util.UUID;

/**
 * Command DTO for creating an Entity.
 *
 * @param cnpjString the CNPJ of the entityId as a string.
 * @param name the name of the entityId.
 * @param cityId the ID of the city where the entityId is located.
 * @param address the address where the entityId is located.
 */
public record EntityCreateCommand(String cnpjString, String name, UUID cityId, String address) {}
