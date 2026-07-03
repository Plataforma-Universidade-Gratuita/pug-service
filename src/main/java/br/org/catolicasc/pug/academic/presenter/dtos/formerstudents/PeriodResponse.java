/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.dtos.formerstudents;

import java.time.LocalDate;

/**
 * Response DTO describing the former student's academic period window.
 *
 * @param startDate raw start date
 * @param startDateFormatted localized start-date string
 * @param dueDate raw due date
 * @param dueDateFormatted localized due-date string
 * @param remainingDays numeric amount of days remaining until the due date
 * @param remainingDaysFormatted localized remaining-days summary
 */
public record PeriodResponse(
    LocalDate startDate,
    String startDateFormatted,
    LocalDate dueDate,
    String dueDateFormatted,
    long remainingDays,
    String remainingDaysFormatted) {}
