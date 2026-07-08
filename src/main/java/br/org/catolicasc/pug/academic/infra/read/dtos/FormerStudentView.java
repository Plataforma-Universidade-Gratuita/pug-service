package br.org.catolicasc.pug.academic.infra.read.dtos;

import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only, flattened view of an enrolled FormerStudent.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client. It exposes only the identifiers and scalar attributes intrinsic to the FormerStudent
 * entity.
 *
 * @param accountId the unique identifier (UUIDv7) of the formerStudent's authentication account
 * @param academicRegistration the raw academic registration string of the formerStudent
 * @param campus the designated university campus where the formerStudent is enrolled
 * @param courseId the unique identifier (UUIDv7) of the enrolled course
 * @param requiredHours the total counterpart hours the formerStudent is required to fulfill
 * @param completedHours the total counterpart hours the formerStudent has completed
 * @param concluded flag indicating whether the required counterpart hours have been completed
 * @param startDate the date when the formerStudent's enrollment period begins
 * @param dueDate the date when the formerStudent's enrollment period expires
 * @param createdAt the exact timestamp when the formerStudent record was initially created
 * @param updatedAt the exact timestamp when the formerStudent record was last modified
 */
public record FormerStudentView(
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
