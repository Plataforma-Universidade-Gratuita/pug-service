package com.pug.projects.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.academic.domain.Student;
import com.pug.projects.domain.enums.AttendanceStatus;
import com.pug.projects.domain.enums.ProjectsFieldErrorCodes;
import com.pug.projects.domain.vos.AttendanceInfo;
import com.pug.projects.domain.vos.EnrollmentIdentifier;
import com.pug.projects.domain.vos.QrValidationInfo;
import com.pug.shared.domain.DomainError;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Immutable Domain Entity representing a Student's Attendance record for a Project.
 *
 * <p>This class acts as an aggregate containing the unique identifier, the linked enrollment, the
 * geolocation data (QR Validation), and the staff validation metadata. It extends {@link
 * DomainError} to accumulate structural validation failures.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Attendance extends DomainError {

  /** The unique identifier for the attendance record (UUIDv7). */
  UUID id;

  /** The composite identifier linking this attendance to a specific student and project. */
  EnrollmentIdentifier enrollmentIdentifier;

  /** The geographic and temporal data recorded when the QR code was scanned. */
  QrValidationInfo qrValidationInfo;

  /** The metadata tracking which staff member validated the attendance and when. */
  AttendanceInfo attendanceInfo;

  /** The current validation status of the attendance (e.g., WAITING, PRESENT). */
  AttendanceStatus status;

  /**
   * Factory method to create a new, unvalidated {@code Attendance} instance.
   *
   * <p>Initializes the attendance in a {@code WAITING} state with empty validation info.
   *
   * @param project the associated project
   * @param student the associated student
   * @param duration the duration of time the student spent on the project
   * @return a newly created and self-validated {@link Attendance} instance
   */
  public static Attendance factory(Project project, Student student, BigDecimal duration) {
    Attendance att =
        Attendance.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .enrollmentIdentifier(
                EnrollmentIdentifier.factory(student.getAccountId(), project.getId()))
            .qrValidationInfo(QrValidationInfo.factory(duration, null, null, null))
            .attendanceInfo(AttendanceInfo.factory(null, null))
            .status(AttendanceStatus.WAITING)
            .build();

    att.collectValidationProblems();
    return att;
  }

  /**
   * Validates the attendance record, transitioning its status and recording validation metadata.
   *
   * <p>This behavior simulates a staff member confirming the student's geographic presence and time
   * spent via a QR code scan.
   *
   * @param validatorId the unique identifier of the staff account performing the validation
   * @param lat the latitude recorded at validation time
   * @param lon the longitude recorded at validation time
   * @param hash the unique hash of the scanned QR code
   * @return a new {@link Attendance} instance reflecting the validated state
   */
  public Attendance validateAttendance(
      UUID validatorId, BigDecimal lat, BigDecimal lon, String hash) {

    QrValidationInfo newQr =
        QrValidationInfo.factory(this.qrValidationInfo.getDuration(), lat, lon, hash);

    AttendanceInfo newInfo =
        this.attendanceInfo.toBuilder()
            .validatedBy(validatorId)
            .validatedAt(OffsetDateTime.now())
            .auditInfo(this.attendanceInfo.getAuditInfo().update())
            .build();

    Attendance updated =
        this.toBuilder()
            .qrValidationInfo(newQr)
            .attendanceInfo(newInfo)
            .status(AttendanceStatus.PRESENT)
            .build();

    updated.collectValidationProblems();
    return updated;
  }

  /** Evaluates constraints for the Attendance aggregate and accumulates any validation problems. */
  private void collectValidationProblems() {
    validateIdField(id);
    if (enrollmentIdentifier == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_PROJECT_BLANK);
      addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_STUDENT_BLANK);
    } else if (enrollmentIdentifier.hasFieldErrors()) {
      addFieldErrors(enrollmentIdentifier.getFieldErrors());
    }
    if (status == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_STATUS_BLANK);
    }
    if (qrValidationInfo != null && qrValidationInfo.hasFieldErrors()) {
      addFieldErrors(qrValidationInfo.getFieldErrors());
    }
    if (attendanceInfo != null && attendanceInfo.hasFieldErrors()) {
      addFieldErrors(attendanceInfo.getFieldErrors());
    }
  }
}
