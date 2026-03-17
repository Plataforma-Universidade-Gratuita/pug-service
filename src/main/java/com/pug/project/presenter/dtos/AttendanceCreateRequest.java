package com.pug.project.presenter.dtos;

import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record AttendanceCreateRequest(
    @NotNull @UuidV7 UUID projectId,
    @NotNull @UuidV7 UUID studentId,
    @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal duration) {}
