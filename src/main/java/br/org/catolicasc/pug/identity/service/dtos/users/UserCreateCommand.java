package br.org.catolicasc.pug.identity.service.dtos.users;

import br.org.catolicasc.pug.identity.domain.User;

/**
 * Data Transfer Object (DTO) acting as an application command to provision a new User identity.
 *
 * <p>This record encapsulates the raw input data required by the application service to instantiate
 * a new {@link User} aggregate.
 *
 * @param cpfString the raw 11-digit Brazilian CPF string belonging to the person
 * @param name the full name of the person
 */
public record UserCreateCommand(String cpfString, String name) {}
