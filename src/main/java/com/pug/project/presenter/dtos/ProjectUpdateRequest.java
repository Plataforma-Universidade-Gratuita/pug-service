package com.pug.project.presenter.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProjectUpdateRequest(
    @Size(max = 150) String name,
    @Size(max = 4000) String description,
    Integer maxParticipants,
    @DecimalMin("0.00") BigDecimal offeredHours) {}
