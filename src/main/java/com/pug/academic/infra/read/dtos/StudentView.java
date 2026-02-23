package com.pug.academic.infra.read.dtos;

import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * DTO for Student read operations.
 *
 * @param account the account of the student
 * @param academicRegistration the academic registration of the student
 * @param campus the campus of the student
 * @param course the course of the student
 * @param requiredHours the required hours for the student
 * @param concluded the status telling if the required hours were concluded or not
 * @param startDate the start date of the student
 * @param dueDate the due date of the student
 * @param createdAt the creation time of the student
 * @param updatedAt the update time of the student
 */
public record StudentView(
    AccountView account,
    String academicRegistration,
    Campi campus,
    CourseView course,
    BigDecimal requiredHours,
    Boolean concluded,
    LocalDate startDate,
    LocalDate dueDate,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
