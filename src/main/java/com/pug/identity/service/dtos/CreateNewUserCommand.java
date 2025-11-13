package com.pug.identity.service.dtos;

import com.pug.identity.domain.vos.Cpf;

/**
 * Command object for creating a new user.
 *
 * @param cpf the CPF of the user
 * @param name the name of the user
 */
public record CreateNewUserCommand(Cpf cpf, String name) {}
