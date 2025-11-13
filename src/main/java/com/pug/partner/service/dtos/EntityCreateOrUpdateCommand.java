package com.pug.partner.service.dtos;

import com.pug.partner.domain.vos.Cnpj;
import java.util.UUID;

/**
 * Command object for creating or updating an Entity.
 *
 * @param name the name of the entity
 * @param cnpj the CNPJ of the entity
 * @param address the address of the entity
 * @param cityId the UUID of the city where the entity is located
 */
public record EntityCreateOrUpdateCommand(String name, Cnpj cnpj, String address, UUID cityId) {}
