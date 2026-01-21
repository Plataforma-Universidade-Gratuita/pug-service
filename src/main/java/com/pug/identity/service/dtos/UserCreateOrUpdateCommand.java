package com.pug.identity.service.dtos;

/**
 * Command DTO for creating or updating a User.
 *
 * @param cpfString the CPF of the user as a string.
 * @param name the name of the user.
 */
public record UserCreateOrUpdateCommand(String cpfString, String name) {}
