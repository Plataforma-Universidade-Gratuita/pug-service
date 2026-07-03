/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.presenter.dtos.enrollments;

import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.time.OffsetDateTime;

/**
 * Nested response describing enrollment lifecycle timestamps and audit metadata.
 *
 * <p>This object groups the non-status enrollment metadata so parent responses can keep a cleaner
 * top-level shape.
 */
public record EnrollmentInfoResponse(
    OffsetDateTime acceptedAt,
    String acceptedAtFormatted,
    OffsetDateTime closingStatusAt,
    String closingStatusAtFormatted,
    AuditInfoResponse auditInfo) {}
