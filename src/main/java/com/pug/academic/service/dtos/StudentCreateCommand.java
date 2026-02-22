package com.pug.academic.service.dtos;

import com.pug.shared.domain.enums.Campi;
import com.pug.identity.service.dtos.AccountCreateCommand;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Command DTO for creating a new Student.
 *
 * @param accountCreateCommand The command to create the associated account.
 * @param academicRegistration The academic registration number for the student.
 * @param campus The campus where the student is enrolled.
 * @param courseId The ID of the course the student is enrolled in.
 * @param requiredHours The total required counterpart hours for the student.
 * @param completedHours The total completed counterpart hours for the student.
 * @param startDate The start date of the academic period.
 * @param dueDate The due date of the academic period.
 */
public record StudentCreateCommand(
    AccountCreateCommand accountCreateCommand,
    String academicRegistration,
    Campi campus,
    UUID courseId,
    BigDecimal requiredHours,
    BigDecimal completedHours,
    LocalDate startDate,
    LocalDate dueDate) {}
