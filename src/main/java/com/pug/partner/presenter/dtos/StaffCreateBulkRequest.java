package com.pug.partner.presenter.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request to create bulk staff members for a specific entity.
 *
 * @param staffCreateRequests the requests containing the data to create the staff members.
 * @param entityCnpjString    the CNPJ of the entity the staff is part of, as a string.
 */
public record StaffCreateBulkRequest(
        @NotEmpty List<@Valid StaffCreateRequest> staffCreateRequests,
        @NotBlank String entityCnpjString) {
}