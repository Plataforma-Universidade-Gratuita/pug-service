package com.pug.partner.service.dtos;

import com.pug.geo.domain.vos.IbgeCode;
import com.pug.partner.domain.vos.Cnpj;

/**
 * Command object for creating or updating an Entity.
 *
 * @param name the name of the entity
 * @param cnpj the CNPJ of the entity
 * @param address the address of the entity
 * @param cityIbge the IBGE code of the city where the entity is located
 */
public record EntityCreateOrUpdateCommand(
    String name, Cnpj cnpj, String address, IbgeCode cityIbge) {}
