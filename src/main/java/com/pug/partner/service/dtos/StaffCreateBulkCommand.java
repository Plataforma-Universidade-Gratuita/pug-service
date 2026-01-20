package com.pug.partner.service.dtos;

import com.pug.identity.service.dtos.AccountCreateCommand;

import java.util.List;

/**
 * Command DTO for creating multiple Staff members in bulk.
 *
 * @param entityCnpjString the CNPJ of the entity as a string.
 * @param accountCommands  the list of commands for creating associated accounts.
 */
public record StaffCreateBulkCommand(
        String entityCnpjString,
        List<AccountCreateCommand> accountCommands) {
}