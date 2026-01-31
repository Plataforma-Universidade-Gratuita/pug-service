package com.pug.partner.presenter.dtos;

import java.util.UUID;

/**
 * Request DTO for updating an Entity.
 *
 * @param cnpjString the CNPJ of the entity as a string.
 * @param name the name of the entity.
 * @param cityId the ID of the city associated with the entity, as a UUID.
 * @param address the address of the entity.
 */
public record EntityUpdateRequest(String cnpjString, String name, UUID cityId, String address) {}
