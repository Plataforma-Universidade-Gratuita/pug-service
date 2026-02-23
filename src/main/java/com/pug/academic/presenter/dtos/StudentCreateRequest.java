package com.pug.academic.presenter.dtos;

import com.pug.shared.domain.enums.Campi;
import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating a new Student.
 *
 * @param cpf the student's CPF
 * @param name the student's name
 * @param email the student's email
 * @param password the student's password
 * @param academicRegistration the academic registration
 * @param campus the campus
 * @param courseId the course ID
 * @param requiredHours the required hours
 * @param startDate the start date
 * @param dueDate the due date
 */
public record StudentCreateRequest(
    @NotBlank @Size(max = 11) String cpf,
    @NotBlank @Size(max = 150) String name,
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Size(min = 8, max = 255) String password,
    @NotBlank @Size(max = 15) String academicRegistration,
    @NotBlank Campi campus,
    @NotNull @UuidV7 UUID courseId,
    @NotNull @DecimalMin("0.00") BigDecimal requiredHours,
    @NotNull LocalDate startDate,
    @NotNull LocalDate dueDate) {}
