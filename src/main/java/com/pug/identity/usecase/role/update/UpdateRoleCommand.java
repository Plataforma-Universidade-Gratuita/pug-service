package com.pug.identity.usecase.role.update;

import com.pug.identity.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateRoleCommand(
    @NotNull UUID id, @NotBlank @Email @Size(max = 254) String email, @NotNull UserRole role) {}
