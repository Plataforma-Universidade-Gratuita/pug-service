package com.pug.identity.usecase.role.get.byEmail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GetRoleByEmailQuery(@NotNull @Email @Size(max = 254) String email) {}
