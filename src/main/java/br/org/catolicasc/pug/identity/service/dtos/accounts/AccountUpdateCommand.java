package br.org.catolicasc.pug.identity.service.dtos.accounts;

import br.org.catolicasc.pug.identity.service.dtos.users.UserUpdateCommand;

/**
 * Data Transfer Object (DTO) acting as an application command to update an existing Account.
 *
 * <p>This record encapsulates the requested state changes for an account. The fields are typically
 * treated as optional during the update process—meaning if a field is omitted (null or empty), the
 * application service will retain the existing value for that specific attribute.
 *
 * @param emailString the new email address string, or {@code null} to leave unchanged
 * @param passwordHash the new hashed password string, or {@code null} to leave unchanged
 * @param active the new activation flag, or {@code null} to leave the account status unchanged
 * @param userCommand the nested command for updating the associated user, or {@code null} to leave
 *     unchanged
 */
public record AccountUpdateCommand(
    String emailString, String passwordHash, Boolean active, UserUpdateCommand userCommand) {}
