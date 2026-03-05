package com.pug.academic.infra.read.dtos;

import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.shared.domain.enums.Campi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Data Transfer Object (DTO) representing a read-only, consolidated view of an enrolled Student.
 * <p>
 * Following CQRS principles, this record is used exclusively for returning queried data
 * to the client. It flattens complex domain value objects and nests the underlying
 * authentication identity ({@link AccountView}) alongside academic details ({@link CourseView})
 * into a comprehensive structure optimized for JSON serialization.
 *
 * @param account              the consolidated, client-facing projection of the authentication account and user profile
 * @param academicRegistration the raw academic registration string of the student
 * @param campus               the designated university campus where the student is enrolled
 * @param course               the consolidated, client-facing projection of the course the student is taking
 * @param requiredHours        the required counterpart hours the student must fulfill
 * @param concluded            a flag indicating whether the required hours have been successfully completed
 * @param startDate            the date when the student's enrollment period begins
 * @param dueDate              the date when the student's enrollment period expires
 * @param createdAt            the exact timestamp when the student record was initially created
 * @param updatedAt            the exact timestamp when the student record was last modified
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
        OffsetDateTime updatedAt) {
}