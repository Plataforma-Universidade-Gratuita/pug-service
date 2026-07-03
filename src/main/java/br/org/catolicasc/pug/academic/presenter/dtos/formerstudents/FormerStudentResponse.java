/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.dtos.formerstudents;

import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.CampusResponse;
import java.util.UUID;

/**
 * Standard API response returned by former-student read endpoints.
 *
 * @param accountId linked account identifier
 * @param academicRegistration academic registration string
 * @param campus localized campus projection
 * @param courseId linked course identifier
 * @param counterpartHours counterpart-hours summary
 * @param period academic-period summary
 * @param auditInfo audit timestamps
 */
public record FormerStudentResponse(
    UUID accountId,
    String academicRegistration,
    CampusResponse campus,
    UUID courseId,
    CounterpartHoursResponse counterpartHours,
    PeriodResponse period,
    AuditInfoResponse auditInfo) {}
