package com.pug.partner.presenter.dtos;

import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Request to create bulk staff members for a specific entity.
 *
 * @param staffCreateOrUpdateRequests the requests containing the data to create the staff members.
 * @param entityId            the ID of the entity to which the staff members will be assigned.
 */
public record StaffCreateBulkRequest(
        @NotEmpty List<StaffCreateOrUpdateRequest> staffCreateOrUpdateRequests,
        @NotNull @UuidV7 UUID entityId
) {
}
