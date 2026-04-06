package com.pug.academic.infra.read.dtos;

import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only, flattened view of an enrolled Student.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client. It consolidates identity and academic data into a single structure without nesting other
 * view types (such as {@code AccountView}, {@code CourseView} or {@code SchoolView}). Instead, it
 * exposes only the essential identifiers and scalar attributes required by the presentation layer.
 *
 * @param accountId the unique identifier (UUIDv7) of the student's authentication account
 * @param userId the unique identifier (UUIDv7) of the user linked to the student's account
 * @param email the email address registered to the student's account
 * @param accountType the designated role of the account (typically {@link AccountType#STUDENT})
 * @param accountActive flag indicating whether the underlying account is currently active
 * @param academicRegistration the raw academic registration string of the student
 * @param campus the designated university campus where the student is enrolled
 * @param courseId the unique identifier (UUIDv7) of the enrolled course
 * @param courseName the name of the enrolled course
 * @param schoolId the unique identifier (UUIDv7) of the school offering the course
 * @param schoolName the name of the school offering the course
 * @param requiredHours the total counterpart hours the student is required to fulfill
 * @param concluded flag indicating whether the required counterpart hours have been completed
 * @param startDate the date when the student's enrollment period begins
 * @param dueDate the date when the student's enrollment period expires
 * @param createdAt the exact timestamp when the student record was initially created
 * @param updatedAt the exact timestamp when the student record was last modified
 */
public record StudentView(
    UUID accountId,
    UUID userId,
    String email,
    AccountType accountType,
    Boolean accountActive,
    String academicRegistration,
    Campi campus,
    UUID courseId,
    String courseName,
    UUID schoolId,
    String schoolName,
    BigDecimal requiredHours,
    Boolean concluded,
    LocalDate startDate,
    LocalDate dueDate,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
