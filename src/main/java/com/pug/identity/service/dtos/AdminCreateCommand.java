package com.pug.identity.service.dtos;

import com.pug.shared.domain.enums.Campi;

/**
 * Command to create a new admin account.
 *
 * @param accountCommand the command containing the data to create the underlying account.
 * @param campus the campus associated with the admin account.
 */
public record AdminCreateCommand(AccountCreateCommand accountCommand, Campi campus) {}
