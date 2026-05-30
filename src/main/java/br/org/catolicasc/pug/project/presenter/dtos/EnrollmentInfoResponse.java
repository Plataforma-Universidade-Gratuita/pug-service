package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.time.OffsetDateTime;

public record EnrollmentInfoResponse(
    OffsetDateTime acceptedAt,
    String acceptedAtFormatted,
    OffsetDateTime closingStatusAt,
    String closingStatusAtFormatted,
    AuditInfoResponse auditInfo) {}
