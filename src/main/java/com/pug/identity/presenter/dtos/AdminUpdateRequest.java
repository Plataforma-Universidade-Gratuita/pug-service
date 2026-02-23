package com.pug.identity.presenter.dtos;

import com.pug.shared.domain.enums.Campi;

/**
 * Request DTO for creating or updating an Admin.
 *
 * @param cpfString the CPF number as a string.
 * @param name the name of the account.
 * @param emailString the email of the account as a string.
 * @param password the password of the account.
 * @param campus the campus where the account is associated with.
 */
public record AdminUpdateRequest(
    String cpfString, String name, String emailString, String password, Campi campus) {}
