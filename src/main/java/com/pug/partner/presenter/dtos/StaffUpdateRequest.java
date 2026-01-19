package com.pug.partner.presenter.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Request to create a new staff member.
 *
 * @param cpf the CPF of the staff member
 * @param name the name of the staff member
 * @param email the email of the staff member
 * @param password the password for the staff member's account
 */
public record StaffUpdateRequest(
    String cpf,
    @Size(max = 150) String name,
    @Email @Size(max = 254) String email,
    @Size(min = 8, max = 255) String password) {}
