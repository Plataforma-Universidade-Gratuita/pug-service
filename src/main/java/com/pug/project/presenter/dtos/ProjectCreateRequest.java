package com.pug.project.presenter.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record ProjectCreateRequest(
    @NotBlank @Size(max = 150) String name,
    @NotNull UUID entityId,
    @NotBlank @Size(max = 4000) String description,
    @NotNull UUID createdBy,
    @Min(0) Integer maxParticipants,
    @NotNull @DecimalMin("0.00") BigDecimal offeredHours) {}
