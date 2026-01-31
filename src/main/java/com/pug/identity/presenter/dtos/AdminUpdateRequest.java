package com.pug.identity.presenter.dtos;

/**
 * Request DTO for creating or updating an Admin.
 *
 * @param cpfString the CPF number as a string.
 * @param name the name of the user.
 * @param emailString the email of the user as a string.
 * @param password the password of the user.
 */
public record AdminUpdateRequest(
    String cpfString, String name, String emailString, String password) {}
