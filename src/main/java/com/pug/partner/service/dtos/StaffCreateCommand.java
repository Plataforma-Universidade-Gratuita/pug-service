package com.pug.partner.service.dtos;

import com.pug.identity.service.dtos.AccountCreateCommand;

/**
 * Command DTO for creating a new Staff member.
 *
 * @param entityCnpjString the CNPJ of the entity as a string.
 * @param accountCommand the command for creating the associated account.
 */
public record StaffCreateCommand(
        String entityCnpjString,
        AccountCreateCommand accountCommand) {}