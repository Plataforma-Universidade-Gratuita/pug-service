/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.presenter.dtos.formerstudents;

import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for enrolling a new FormerStudent.
 *
 * <p>This record acts as an aggregated payload that combines identity credentials with academic
 * enrollment details. It applies Jakarta Bean Validation constraints to ensure all required
 * components are structurally sound before attempting a complex, multi-domain transaction.
 *
 * @param cpf the raw 11-digit numeric CPF string of the formerStudent (must not be blank)
 * @param name the full name of the formerStudent (must not be blank and max 150 characters)
 * @param email the email address for the former-student account (must be a valid format and max 254
 *     characters)
 * @param academicRegistration the university-issued academic registration string (must not be blank
 *     and max 15 characters)
 * @param campus the designated university campus enum where the formerStudent is enrolled (must not
 *     be null)
 * @param courseId the unique identifier (UUIDv7) of the enrolled course (must not be null)
 * @param requiredHours the required counterpart hours the formerStudent must complete (must be
 *     non-negative)
 * @param startDate the start date of the enrollment period (must not be null)
 * @param dueDate the due date (end date) of the enrollment period (must not be null)
 */
public record FormerStudentCreateRequest(
    @NotBlank @Size(max = 11) String cpf,
    @NotBlank @Size(max = 150) String name,
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Size(max = 15) String academicRegistration,
    @NotNull Campi campus,
    @NotNull @UuidV7 UUID courseId,
    @NotNull @DecimalMin("0.00") BigDecimal requiredHours,
    @NotNull LocalDate startDate,
    @NotNull LocalDate dueDate) {}
