package com.pug.academic.presenter.dtos;

import com.pug.identity.presenter.dtos.AccountResponse;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * StudentResponse record.
 *
 * @param user the user information
 * @param academicRegistration the academic registration
 * @param campus the campus
 * @param course the course
 * @param requiredHours the required hours
 * @param completedHours the completed hours
 * @param missingHours the missing hours
 * @param startDate the start date
 * @param startDateFormatted the formatted start date
 * @param dueDate the due date
 * @param dueDateFormatted the formatted due date
 * @param remainingDays the number of remaining days until due date
 * @param remainingDaysFormatted the formatted remaining days (e.g., "X dias")
 */
public record StudentResponse(
    AccountResponse user,
    String academicRegistration,
    String campus,
    CourseResponse course,
    BigDecimal requiredHours,
    BigDecimal completedHours,
    BigDecimal missingHours,
    LocalDate startDate,
    String startDateFormatted,
    LocalDate dueDate,
    String dueDateFormatted,
    long remainingDays,
    String remainingDaysFormatted) {}
