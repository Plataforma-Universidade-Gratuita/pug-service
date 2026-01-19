package com.pug.partner.service.dtos;

import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.partner.domain.vos.Cnpj;

/**
 * Command to create bulk staff members for a specific entity.
 *
 * @param accountCommands the commands containing the data to create the underlying accounts.
 * @param entityCnpj the CNPJ of the entity to which the staff members will be linked.
 */
public record StaffCreateBulkCommand(
    Iterable<AccountCreateCommand> accountCommands, Cnpj entityCnpj) {}
