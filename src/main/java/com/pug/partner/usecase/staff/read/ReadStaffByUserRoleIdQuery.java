package com.pug.partner.usecase.staff.read;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReadStaffByUserRoleIdQuery(@NotNull UUID userRoleId) {}
