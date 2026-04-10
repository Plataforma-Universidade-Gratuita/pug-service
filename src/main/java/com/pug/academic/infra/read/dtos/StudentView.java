package com.pug.academic.infra.read.dtos;

import com.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only, flattened view of an enrolled Student.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client. It exposes only the identifiers and scalar attributes intrinsic to the Student entity.
 *
 * @param accountId the unique identifier (UUIDv7) of the student's authentication account
 * @param academicRegistration the raw academic registration string of the student
 * @param campus the designated university campus where the student is enrolled
 * @param courseId the unique identifier (UUIDv7) of the enrolled course
 * @param requiredHours the total counterpart hours the student is required to fulfill
 * @param completedHours the total counterpart hours the student has completed
 * @param concluded flag indicating whether the required counterpart hours have been completed
 * @param startDate the date when the student's enrollment period begins
 * @param dueDate the date when the student's enrollment period expires
 * @param createdAt the exact timestamp when the student record was initially created
 * @param updatedAt the exact timestamp when the student record was last modified
 */
public record StudentView(
    UUID accountId,
    String academicRegistration,
    Campi campus,
    UUID courseId,
    BigDecimal requiredHours,
    BigDecimal completedHours,
    Boolean concluded,
    LocalDate startDate,
    LocalDate dueDate,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
