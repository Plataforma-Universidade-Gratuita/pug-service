package com.pug.partner.usecase.staff.get;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RetrieveStaffByUserRoleIdQuery(@NotNull UUID userRoleId) {}
