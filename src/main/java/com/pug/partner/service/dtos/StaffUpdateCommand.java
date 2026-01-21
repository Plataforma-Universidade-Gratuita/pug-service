package com.pug.partner.service.dtos;

import com.pug.identity.service.dtos.AccountUpdateCommand;

/**
 * Command DTO for updating an existing Staff member.
 *
 * @param entityCnpjString the new CNPJ of the entity as a string (optional).
 * @param accountCommand the command for updating the associated account (optional).
 */
public record StaffUpdateCommand(String entityCnpjString, AccountUpdateCommand accountCommand) {}
