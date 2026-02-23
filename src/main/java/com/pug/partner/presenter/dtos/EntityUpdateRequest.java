package com.pug.partner.presenter.dtos;

import java.util.UUID;

/**
 * Request DTO for updating an Entity.
 *
 * @param cnpjString the CNPJ of the entityId as a string.
 * @param name the name of the entityId.
 * @param cityId the ID of the city associated with the entityId, as a UUID.
 * @param address the address of the entityId.
 */
public record EntityUpdateRequest(String cnpjString, String name, UUID cityId, String address) {}
