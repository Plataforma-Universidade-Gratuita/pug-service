package com.pug.project.presenter.dtos;

import com.pug.academic.presenter.dtos.StudentResponse;
import com.pug.project.domain.enums.EnrollmentStatus;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import java.time.OffsetDateTime;

public record EnrollmentResponse(
    ProjectResponse project,
    StudentResponse student,
    EnrollmentStatus status,
    String statusFormatted,
    OffsetDateTime acceptedAt,
    String acceptedAtFormatted,
    OffsetDateTime closingStatusAt,
    String closingStatusAtFormatted,
    AuditInfoResponse auditInfo) {}
