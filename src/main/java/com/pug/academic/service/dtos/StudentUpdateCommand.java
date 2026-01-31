package com.pug.academic.service.dtos;

import com.pug.academic.domain.enums.Campi;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Command DTO for updating an existing Student.
 *
 * @param accountUpdateCommand The command to update the associated account (optional).
 * @param academicRegistration The new academic registration number (optional).
 * @param campus The new campus where the student is enrolled (optional).
 * @param courseId The new course ID the student is enrolled in (optional).
 * @param requiredHours The new total required counterpart hours (optional).
 * @param completedHours The new total completed counterpart hours (optional).
 * @param startDate The new start date of the academic period (optional).
 * @param dueDate The new due date of the academic period (optional).
 */
public record StudentUpdateCommand(
    AccountUpdateCommand accountUpdateCommand,
    String academicRegistration,
    Campi campus,
    UUID courseId,
    BigDecimal requiredHours,
    BigDecimal completedHours,
    LocalDate startDate,
    LocalDate dueDate) {}
