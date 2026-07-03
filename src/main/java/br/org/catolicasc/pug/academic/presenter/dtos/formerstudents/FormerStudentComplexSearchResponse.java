/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.dtos.formerstudents;

import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseComplexSearchResponse;
import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountComplexSearchResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.CampusResponse;

/**
 * Response DTO returned by the former-student complex-search endpoint.
 *
 * @param account lightweight account projection associated with the former student
 * @param academicRegistration academic registration string
 * @param campus localized campus projection
 * @param counterpartHours counterpart-hours progress summary
 * @param period period summary
 * @param auditInfo audit timestamps
 * @param course lightweight course projection
 */
public record FormerStudentComplexSearchResponse(
    AccountComplexSearchResponse account,
    String academicRegistration,
    CampusResponse campus,
    CounterpartHoursResponse counterpartHours,
    PeriodResponse period,
    AuditInfoResponse auditInfo,
    CourseComplexSearchResponse course) {}
