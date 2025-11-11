package com.pug.academic.presenter.dtos;

import com.pug.identity.presenter.dtos.UserResponse;
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
 * @param startDateLabel the start date label
 * @param dueDate the due date
 * @param dueDateLabel the due date label
 * @param remainingDays the remaining days
 * @param remainingDaysLabel the remaining days label
 */
public record StudentResponse(
    UserResponse user,
    String academicRegistration,
    String campus,
    CourseResponse course,
    BigDecimal requiredHours,
    BigDecimal completedHours,
    BigDecimal missingHours,
    LocalDate startDate,
    String startDateLabel,
    LocalDate dueDate,
    String dueDateLabel,
    LocalDate remainingDays,
    String remainingDaysLabel) {}
