package com.pug.projects.infra.read.dtos;

import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.projects.domain.enums.EnrollmentStatus;

import java.time.OffsetDateTime;

/**
 * Data Transfer Object (DTO) representing a read-only view of a Student's Enrollment in a Project.
 * <p>
 * Following CQRS principles, this record flattens the many-to-many relationship between
 * Students and Projects. It nests the full {@link ProjectView} and {@link StudentView}
 * alongside the specific lifecycle metadata of the enrollment.
 *
 * @param project         the nested read-only projection of the associated project
 * @param student         the nested read-only projection of the enrolled student
 * @param status          the current lifecycle status of the enrollment
 * @param createdAt       the exact timestamp when the enrollment request was created
 * @param updatedAt       the exact timestamp when the enrollment record was last modified
 * @param acceptedAt      the exact timestamp when the enrollment was formally approved
 * @param closingStatusAt the exact timestamp when the enrollment reached a terminal state
 */
public record EnrollmentView(
        ProjectView project,
        StudentView student,
        EnrollmentStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime acceptedAt,
        OffsetDateTime closingStatusAt) {
}