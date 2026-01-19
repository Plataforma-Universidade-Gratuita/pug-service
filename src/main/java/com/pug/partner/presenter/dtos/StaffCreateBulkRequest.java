package com.pug.partner.presenter.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Request to create bulk staff members for a specific entity.
 *
 * @param staffCreateRequests the requests containing the data to create the staff members.
 * @param entityCnpj the CNPJ of the entity the staff is part of.
 */
public record StaffCreateBulkRequest(
    @NotEmpty List<StaffCreateRequest> staffCreateRequests, @NotNull String entityCnpj) {}
