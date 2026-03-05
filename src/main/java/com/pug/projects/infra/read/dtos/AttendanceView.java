package com.pug.projects.infra.read.dtos;

import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.projects.domain.enums.AttendanceStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only view of an Attendance record.
 *
 * <p>Following CQRS principles, this record provides a flattened view of a student's participation
 * in a project, including geographic validation data and the staff member who approved the
 * attendance.
 *
 * @param id the unique identifier (UUIDv7) of the attendance record
 * @param project the nested read-only projection of the associated project
 * @param student the nested read-only projection of the attending student
 * @param duration the recorded time duration the student spent on the project
 * @param latitude the geographic latitude where the attendance was recorded
 * @param longitude the geographic longitude where the attendance was recorded
 * @param qrValidationHash the unique cryptographic hash of the QR code used
 * @param status the current validation status of the attendance
 * @param validatedBy the nested read-only projection of the staff account who validated the record
 * @param validatedAt the exact timestamp when the attendance was validated
 * @param createdAt the exact timestamp when the attendance record was initially created
 * @param updatedAt the exact timestamp when the attendance record was last modified
 */
public record AttendanceView(
    UUID id,
    ProjectView project,
    StudentView student,
    BigDecimal duration,
    BigDecimal latitude,
    BigDecimal longitude,
    String qrValidationHash,
    AttendanceStatus status,
    AccountView validatedBy,
    OffsetDateTime validatedAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
