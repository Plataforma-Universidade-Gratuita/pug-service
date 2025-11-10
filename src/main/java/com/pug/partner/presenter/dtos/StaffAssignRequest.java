package com.pug.partner.presenter.dtos;

import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** Payload to assign an existing user as staff. */
public record StaffAssignRequest(@NotNull @UuidV7 UUID userId, @NotNull @UuidV7 UUID entityId) {}
