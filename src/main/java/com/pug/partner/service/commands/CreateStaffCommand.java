package com.pug.partner.service.commands;

import com.pug.shared.domain.validation.EmailBasic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateStaffCommand(
    @NotNull UUID userId,
    @NotNull UUID entityId,
    @NotBlank @EmailBasic @Size(max = 254) String email) {}
