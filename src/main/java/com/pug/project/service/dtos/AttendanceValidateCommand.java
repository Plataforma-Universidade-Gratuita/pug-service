package com.pug.project.service.dtos;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to validate an existing Attendance.
 */
public record AttendanceValidateCommand(
    UUID validatorId, BigDecimal latitude, BigDecimal longitude, String qrValidationHash) {}
