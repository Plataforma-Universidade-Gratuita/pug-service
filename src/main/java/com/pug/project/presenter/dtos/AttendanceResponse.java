package com.pug.project.presenter.dtos;

import com.pug.academic.presenter.dtos.StudentResponse;
import com.pug.identity.presenter.dtos.AccountResponse;
import com.pug.project.domain.enums.AttendanceStatus;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AttendanceResponse(
    UUID id,
    ProjectResponse project,
    StudentResponse student,
    BigDecimal duration,
    BigDecimal latitude,
    BigDecimal longitude,
    String qrValidationHash,
    AttendanceStatus status,
    String statusFormatted,
    AccountResponse validatedBy,
    OffsetDateTime validatedAt,
    String validatedAtFormatted,
    AuditInfoResponse auditInfo) {}
