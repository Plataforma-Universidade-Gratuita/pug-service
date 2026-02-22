package com.pug.identity.service.dtos;

import com.pug.shared.domain.enums.Campi;

/**
 * Command to update an existing admin user.
 *
 * @param accountCommand the command containing the data to update the underlying account.
 * @param campus the new campus to which the admin comes from.
 */
public record AdminUpdateCommand(AccountUpdateCommand accountCommand, Campi campus) {}
