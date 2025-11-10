package com.pug.partner.presenter.dtos;

import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/** Payload to create a staff user and assign to an entity. */
public record StaffCreateRequest(
    @NotBlank String cpf,
    @NotBlank @Size(max = 150) String name,
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Size(min = 8, max = 255) String password,
    @NotNull @UuidV7 UUID entityId) {}
