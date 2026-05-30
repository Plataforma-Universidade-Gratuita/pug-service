package br.org.catolicasc.pug.project.infra.read.dtos;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

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
