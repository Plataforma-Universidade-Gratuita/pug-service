/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.infra.read.dtos;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Read-side projection used by enrollment queries and presenter mappings.
 *
 * <p>This projection flattens the enrollment, project, former-student, and period information
 * required by both the canonical enrollment response and the enrollment complex-search response.
 */
public record EnrollmentView(
    UUID projectId,
    String projectName,
    UUID formerStudentId,
    String formerStudentName,
    String formerStudentEmail,
    String academicRegistration,
    Campi campus,
    LocalDate startDate,
    LocalDate dueDate,
    EnrollmentStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    OffsetDateTime acceptedAt,
    OffsetDateTime closingStatusAt) {

  /**
   * Creates an enrollment view from a string-backed status projection.
   *
   * @param projectId the linked project identifier
   * @param projectName the linked project name
   * @param formerStudentId the linked former-student account identifier
   * @param studentName the former-student display name
   * @param studentEmail the former-student email address
   * @param academicRegistration the academic registration code
   * @param campus the campus associated with the former student
   * @param startDate the counterpart period start date
   * @param dueDate the counterpart period due date
   * @param status the persisted status name
   * @param createdAt the enrollment creation timestamp
   * @param updatedAt the enrollment last-update timestamp
   * @param acceptedAt the enrollment approval timestamp
   * @param closingStatusAt the final-status timestamp
   */
  public EnrollmentView(
      UUID projectId,
      String projectName,
      UUID formerStudentId,
      String studentName,
      String studentEmail,
      String academicRegistration,
      Campi campus,
      LocalDate startDate,
      LocalDate dueDate,
      String status,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt,
      OffsetDateTime acceptedAt,
      OffsetDateTime closingStatusAt) {
    this(
        projectId,
        projectName,
        formerStudentId,
        studentName,
        studentEmail,
        academicRegistration,
        campus,
        startDate,
        dueDate,
        EnrollmentStatus.valueOf(status),
        createdAt,
        updatedAt,
        acceptedAt,
        closingStatusAt);
  }
}
