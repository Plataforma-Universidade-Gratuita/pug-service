package com.pug.partner.usecase.staff.create;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateStaffCommand(@NotNull UUID userRoleId, @NotNull UUID entityId) {}
