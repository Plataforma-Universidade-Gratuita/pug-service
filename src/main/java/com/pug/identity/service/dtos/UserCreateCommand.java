package com.pug.identity.service.dtos;

/**
 * Command DTO for creating or updating a User.
 *
 * @param cpfString the CPF of the account as a string.
 * @param name the name of the account.
 */
public record UserCreateCommand(String cpfString, String name) {}
