package com.pug.projects.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.academic.domain.Student;
import com.pug.projects.domain.enums.AttendanceStatus;
import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.projects.domain.vos.AttendanceInfo;
import com.pug.projects.domain.vos.QrValidationInfo;
import com.pug.shared.domain.DomainError;
import com.pug.shared.time.TimeProvider;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Domain entity representing an Attendance. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Attendance extends DomainError {
  private final UUID id;
  private final Student student;
  private final Project project;
  private final QrValidationInfo qrValidationInfo;
  private final AttendanceInfo attendanceInfo;
  private final AttendanceStatus status;

  /**
   * Factory method to create a new Attendance instance.
   *
   * @param project the associated Project.
   * @param student the associated Student.
   * @param duration the duration of the attendance.
   * @param time the TimeProvider for current time.
   * @return a validated Attendance instance.
   */
  public static Attendance factory(
      Project project, Student student, BigDecimal duration, TimeProvider time) {

    Attendance att =
        Attendance.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .project(project)
            .student(student)
            .qrValidationInfo(QrValidationInfo.factory(duration, null, null, null))
            .attendanceInfo(AttendanceInfo.factory(null, null, OffsetDateTime.now(time.clock())))
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
   * @param time the TimeProvider for current time.
   * @return a validated Attendance instance with updated information.
   */
  public Attendance validateAttendance(
      UUID validatorId, BigDecimal lat, BigDecimal lon, String hash, TimeProvider time) {
    QrValidationInfo newQr =
        QrValidationInfo.factory(this.qrValidationInfo.getDuration(), lat, lon, hash);

    AttendanceInfo newInfo =
        AttendanceInfo.factory(
            validatorId, OffsetDateTime.now(time.clock()), this.attendanceInfo.getCreatedAt());

    Attendance updated =
        this.toBuilder()
            .qrValidationInfo(newQr)
            .attendanceInfo(newInfo)
            .status(AttendanceStatus.PRESENT)
            .build();

    updated.collectValidationProblems();
    return updated;
  }

  /** Validates the Attendance entity's fields and state. */
  private void collectValidationProblems() {
    if (project == null) {
      addError(new Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_PROJECT_BLANK));
    }
    if (student == null) {
      addError(new Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_STUDENT_BLANK));
    }
    if (status == null) {
      addError(new Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_STATUS_BLANK));
    }

    if (qrValidationInfo != null) {
      if (qrValidationInfo.getDuration() == null || qrValidationInfo.getDuration().signum() <= 0) {
        addError(new Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_DURATION_INVALID));
      }
      BigDecimal lat = qrValidationInfo.getLatitude();
      BigDecimal lon = qrValidationInfo.getLongitude();

      boolean invalidLat =
          lat != null
              && (lat.compareTo(BigDecimal.valueOf(90)) > 0
                  || lat.compareTo(BigDecimal.valueOf(-90)) < 0);
      boolean invalidLon =
          lon != null
              && (lon.compareTo(BigDecimal.valueOf(180)) > 0
                  || lon.compareTo(BigDecimal.valueOf(-180)) < 0);

      if (invalidLat || invalidLon) {
        addError(new Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_GEO_INVALID_MISSING));
      }
    }
  }
}
