package com.pug.academic.presenter.dtos;

import com.pug.identity.presenter.dtos.AccountResponse;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.dtos.CampusResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Student profiles.
 * <p>
 * This record consolidates the deeply nested backend structure across the Identity and
 * Academic domains into a single, comprehensive response representing a student's
 * enrollment. It includes pre-computed business logic (e.g., missing hours, remaining days)
 * and formatted strings optimized for direct UI rendering.
 *
 * @param account                the consolidated, client-facing projection of the authentication account and user profile
 * @param academicRegistration   the raw academic registration string
 * @param campus                 the formatted response object representing the student's assigned campus
 * @param course                 the consolidated, client-facing projection of the enrolled course
 * @param requiredHours          the total required counterpart hours the student must fulfill
 * @param completedHours         the total counterpart hours the student has completed to date
 * @param missingHours           the remaining counterpart hours left to fulfill the requirement
 * @param startDate              the raw date when the enrollment period began
 * @param startDateFormatted     a localized, human-readable string representing the start date
 * @param dueDate                the raw date when the enrollment period expires
 * @param dueDateFormatted       a localized, human-readable string representing the due date
 * @param remainingDays          the numerical count of days remaining until the due date
 * @param remainingDaysFormatted a localized, human-readable string representing the time remaining (e.g., "5 dias")
 * @param auditInfo              the nested audit information containing creation and update timestamps
 */
public record StudentResponse(
        AccountResponse account,
        String academicRegistration,
        CampusResponse campus,
        CourseResponse course,
        BigDecimal requiredHours,
        BigDecimal completedHours,
        BigDecimal missingHours,
        LocalDate startDate,
        String startDateFormatted,
        LocalDate dueDate,
        String dueDateFormatted,
        long remainingDays,
        String remainingDaysFormatted,
        AuditInfoResponse auditInfo) {
}