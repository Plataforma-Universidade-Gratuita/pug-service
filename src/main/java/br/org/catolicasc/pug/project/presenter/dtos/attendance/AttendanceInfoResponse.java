/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.presenter.dtos.attendance;

import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object describing attendance validation metadata and audit history.
 *
 * @param validatedBy the unique identifier of the validating account
 * @param validatedAt the timestamp when the attendance was validated
 * @param validatedAtFormatted the localized, human-readable validation timestamp
 * @param auditInfo the nested audit metadata payload
 */
public record AttendanceInfoResponse(
    UUID validatedBy,
    OffsetDateTime validatedAt,
    String validatedAtFormatted,
    AuditInfoResponse auditInfo) {}
