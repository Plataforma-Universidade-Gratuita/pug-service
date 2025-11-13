package com.pug.identity.service.dtos;

import com.pug.identity.domain.vos.Cpf;

/**
 * Command to update a User.
 *
 * @param cpf the user's new CPF
 * @param name the user's new name
 */
public record UpdateUserCommand(
        Cpf cpf,
        String name
) {
}
