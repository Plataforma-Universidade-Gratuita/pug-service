/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.dtos.formerstudents;

import java.math.BigDecimal;

/**
 * Response DTO describing counterpart-hours progress for a former student.
 *
 * @param requiredHours total required hours
 * @param completedHours total completed hours
 * @param missingHours remaining hours still pending
 * @param progress completion percentage in the {@code 0..100} range
 * @param concluded whether the requirement has already been concluded
 */
public record CounterpartHoursResponse(
    BigDecimal requiredHours,
    BigDecimal completedHours,
    BigDecimal missingHours,
    BigDecimal progress,
    boolean concluded) {}
