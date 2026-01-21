package com.pug.academic.presenter.dtos;

import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
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
 * @param completedHours the new completed hours (optional) // Adicionado para permitir atualização
 * @param startDate the new start date (optional)
 * @param dueDate the new due date (optional)
 */
public record StudentUpdateRequest(
    @Size(max = 150) String name,
    @Email @Size(max = 254) String email,
    @Size(min = 8, max = 255) String password,
    @Size(max = 15) String academicRegistration,
    @Size(max = 150) String campus,
    @UuidV7 UUID courseId,
    @DecimalMin(value = "0.00", inclusive = true) BigDecimal requiredHours,
    @DecimalMin(value = "0.00", inclusive = true) BigDecimal completedHours, // Adicionado
    LocalDate startDate,
    LocalDate dueDate) {}
