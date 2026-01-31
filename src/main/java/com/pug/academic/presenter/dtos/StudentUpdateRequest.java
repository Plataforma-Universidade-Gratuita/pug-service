package com.pug.academic.presenter.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for updating an existing Student. All fields are optional, as they may not be changed.
 *
 * @param name the student's new name (optional)
 * @param email the student's new email (optional)
 * @param password the student's new password (optional)
 * @param academicRegistration the new academic registration (optional)
 * @param campus the new campus (optional)
 * @param courseId the new course ID (optional)
 * @param requiredHours the new required hours (optional)
 * @param completedHours the new completed hours (optional)
 * @param startDate the new start date (optional)
 * @param dueDate the new due date (optional)
 */
public record StudentUpdateRequest(
    String name,
    String email,
    String password,
    String academicRegistration,
    String campus,
    UUID courseId,
    BigDecimal requiredHours,
    BigDecimal completedHours,
    LocalDate startDate,
    LocalDate dueDate) {}
