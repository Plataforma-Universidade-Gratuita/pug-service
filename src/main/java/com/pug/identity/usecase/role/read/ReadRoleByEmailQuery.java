package com.pug.identity.usecase.role.read;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReadRoleByEmailQuery(@NotNull @Email @Size(max = 254) String email) {}
