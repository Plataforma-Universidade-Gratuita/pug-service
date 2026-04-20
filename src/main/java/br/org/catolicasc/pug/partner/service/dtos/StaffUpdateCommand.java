package br.org.catolicasc.pug.partner.service.dtos;

import br.org.catolicasc.pug.identity.service.dtos.AccountUpdateCommand;

/**
 * Data Transfer Object (DTO) acting as an application command to update an existing Staff member's
 * underlying account information.
 *
 * <p>This record encapsulates the requested state changes for a staff member's identity and
 * credentials. The fields within the nested command are treated as optional for partial updates;
 * omitting a value will retain the current state in the database.
 *
 * @param accountCommand the nested command containing the identity and credential updates
 */
public record StaffUpdateCommand(AccountUpdateCommand accountCommand) {}
