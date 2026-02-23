package com.pug.academic.presenter.dtos;

import com.pug.shared.domain.enums.Campi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for updating an existing Student. All fields are optional, as they may not be changed.
 *
 * @param name                 the student's new name (optional)
 * @param cpf                  the student's new CPF (optional)
 * @param email                the student's new email (optional)
 * @param password             the student's new password (optional)
 * @param academicRegistration the new academic registration (optional)
 * @param campus               the new campus (optional)
 * @param courseId             the new course ID (optional)
 * @param requiredHours        the new required hours (optional)
 * @param startDate            the new start date (optional)
 * @param dueDate              the new due date (optional)
 */
public record StudentUpdateRequest(
        String name,
        String cpf,
        String email,
        String password,
        String academicRegistration,
        Campi campus,
        UUID courseId,
        BigDecimal requiredHours,
        LocalDate startDate,
        LocalDate dueDate) {
}
