package com.pug.partner.presenter.dtos;

import java.util.UUID;

/**
 * Request to update an existing staff member.
 *
 * @param cpfString the CPF of the staff member as a string (optional).
 * @param name the name of the staff member (optional).
 * @param emailString the email of the staff member as a string (optional).
 * @param entityId the CNPJ of the associated entity as a UUID (optional).
 * @param password the password for the staff member's account (optional).
 */
public record StaffUpdateRequest(
    String cpfString, String name, String emailString, UUID entityId, String password) {}
