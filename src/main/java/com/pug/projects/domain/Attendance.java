package com.pug.projects.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.academic.domain.Student;
import com.pug.projects.domain.enums.AttendanceStatus;
import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.projects.domain.vos.AttendanceInfo;
import com.pug.projects.domain.vos.EnrollmentIdentifier;
import com.pug.projects.domain.vos.QrValidationInfo;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.Problem;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Domain entityId representing an Attendance. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Attendance extends DomainError {
  UUID id;
  EnrollmentIdentifier enrollmentIdentifier;
  QrValidationInfo qrValidationInfo;
  AttendanceInfo attendanceInfo;
  AttendanceStatus status;

  /**
   * Factory method to create a new Attendance instance.
   *
   * @param project the associated Project.
   * @param student the associated Student.
   * @param duration the duration of the attendance.
   * @return a validated Attendance instance.
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
   * Validates the attendance, updating its status and validation info.
   *
   * @param validatorId the ID of the validator.
   * @param lat the latitude of the validation location.
   * @param lon the longitude of the validation location.
   * @param hash the hash of the QR code.
   * @return a validated Attendance instance with updated information.
   */
  public Attendance validateAttendance(
      UUID validatorId, BigDecimal lat, BigDecimal lon, String hash) {

    // We update QR Info with geo data
    QrValidationInfo newQr =
        QrValidationInfo.factory(this.qrValidationInfo.getDuration(), lat, lon, hash);

    // We create a new AttendanceInfo with validation details, preserving the original createdAt
    // (via factory/builder pattern in VO if supported, or here we are creating fresh but using
    // 'factory' which resets createdAt to NOW inside VO usually.
    // Ideally we should use toBuilder on existing attendanceInfo if we want to preserve createdAt.
    // Assuming AttendanceInfo.factory resets time, we should use toBuilder manually here or update
    // the VO to support 'validate' transition.
    // For now, doing manual rebuild to preserve createdAt from original object:

    AttendanceInfo newInfo =
        this.attendanceInfo.toBuilder()
            .validatedBy(validatorId)
            .validatedAt(OffsetDateTime.now())
            .auditInfo(this.attendanceInfo.getAuditInfo().update()) // Update updatedAt
            .build();
    // Re-validate the VO manually or rely on entity validation below
    // (Ideally VO methods should handle this internal consistency)

    Attendance updated =
        this.toBuilder()
            .qrValidationInfo(newQr)
            .attendanceInfo(newInfo)
            .status(AttendanceStatus.PRESENT)
            .build();

    updated.collectValidationProblems();
    return updated;
  }

  /** Validates the Attendance entityId's fields and state. */
  private void collectValidationProblems() {
    validateIdField(id);

    if (enrollmentIdentifier == null) {
      addError(
          new Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_PROJECT_BLANK)); // Generic fallback
      addError(new Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_STUDENT_BLANK));
    } else if (enrollmentIdentifier.hasErrors()) {
      addErrors(enrollmentIdentifier.getProblems());
    }

    if (status == null) {
      addError(new Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_STATUS_BLANK));
    }

    if (qrValidationInfo != null && qrValidationInfo.hasErrors()) {
      addErrors(qrValidationInfo.getProblems());
    }

    if (attendanceInfo != null && attendanceInfo.hasErrors()) {
      addErrors(attendanceInfo.getProblems());
    }
  }
}
