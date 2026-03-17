package com.pug.project.presenter.dtos;

import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record AttendanceValidateRequest(
    @NotNull @UuidV7 UUID validatorId,
    @NotNull @DecimalMin("-90.000000") @DecimalMax("90.000000") BigDecimal latitude,
    @NotNull @DecimalMin("-180.000000") @DecimalMax("180.000000") BigDecimal longitude,
    @NotBlank @Size(max = 512) String qrValidationHash) {}
