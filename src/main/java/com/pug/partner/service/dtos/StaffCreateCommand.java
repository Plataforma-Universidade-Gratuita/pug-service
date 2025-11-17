package com.pug.partner.service.dtos;

import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.partner.domain.vos.Cnpj;

/**
 * Command to create a new staff member.
 *
 * @param accountCommand the command containing the data to create the underlying account.
 * @param entityCnpj     the CNPJ of the entity to which the staff member will be linked.
 */
public record StaffCreateCommand(
        AccountCreateCommand accountCommand, Cnpj entityCnpj
) {
}
