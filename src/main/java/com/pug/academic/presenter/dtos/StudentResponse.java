package com.pug.academic.presenter.dtos;

import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.dtos.CampusResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Student profiles.
 *
 * <p>This record consolidates identity and academic data into a single, flattened structure
 * optimized for the presentation layer. Instead of nesting the full account or course responses, it
 * exposes only the essential identifiers and scalar attributes required by the UI.
 *
 * @param accountId the unique identifier (UUIDv7) of the student's authentication account
 * @param academicRegistration the raw academic registration string
 * @param campus the formatted response object representing the student's assigned campus
 * @param courseId the unique identifier (UUIDv7) of the enrolled course
 * @param requiredHours the total required counterpart hours the student must fulfill
 * @param completedHours the total counterpart hours the student has completed to date
 * @param missingHours the remaining counterpart hours left to fulfill the requirement
 * @param startDate the raw date when the enrollment period began
 * @param startDateFormatted a localized, human-readable string representing the start date
 * @param dueDate the raw date when the enrollment period expires
 * @param dueDateFormatted a localized, human-readable string representing the due date
 * @param remainingDays the numerical count of days remaining until the due date
 * @param remainingDaysFormatted a localized, human-readable string representing the time remaining
 *     (e.g., "5 dias")
 * @param auditInfo the nested audit information containing creation and update timestamps
 */
public record StudentResponse(
    UUID accountId,
    String academicRegistration,
    CampusResponse campus,
    UUID courseId,
    BigDecimal requiredHours,
    BigDecimal completedHours,
    BigDecimal missingHours,
    LocalDate startDate,
    String startDateFormatted,
    LocalDate dueDate,
    String dueDateFormatted,
    long remainingDays,
    String remainingDaysFormatted,
    AuditInfoResponse auditInfo) {}
