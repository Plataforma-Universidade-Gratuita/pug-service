package com.pug.partner.presenter.dtos;

import jakarta.validation.constraints.Size;

/**
 * Request to update an existing staff member.
 *
 * @param cpfString   the CPF of the staff member as a string (optional).
 * @param name        the name of the staff member (optional).
 * @param emailString the email of the staff member as a string (optional).
 * @param entityCnpjString the CNPJ of the associated entity as a string (optional).
 * @param password    the password for the staff member's account (optional).
 */
public record StaffUpdateRequest(
        String cpfString,
        @Size(max = 150) String name,
        String emailString,
        String entityCnpjString,
        @Size(min = 8, max = 255) String password) {
}