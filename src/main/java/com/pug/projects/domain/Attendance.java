package com.pug.projects.domain;

import com.pug.academic.domain.Student;
import com.pug.projects.domain.enums.AttendanceStatus;
import com.pug.projects.domain.vos.AttendanceInfo;
import com.pug.projects.domain.vos.QrValidationInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Domain entity representing a Course. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Attendance {
  private final Student student;
  private final Project project;
  private final QrValidationInfo qrValidationInfo;
  private final AttendanceInfo attendanceInfo;
  private final AttendanceStatus status;

  private void validate() {}

  /** Builder class for constructing Attendance instances with validation. */
  public static class AttendanceBuilder {
    /**
     * Builds the Attendance instance and performs validation.
     *
     * @return the validated Attendance instance.
     */
    public Attendance build() {
      Attendance c = new Attendance(student, project, qrValidationInfo, attendanceInfo, status);
      c.validate();
      return c;
    }
  }
}
