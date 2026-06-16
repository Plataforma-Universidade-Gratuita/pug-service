package br.org.catolicasc.pug.helpers.builders.domain;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.Project;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;

/**
 * Builder class for creating {@link Attendance} domain aggregates in test scenarios.
 *
 * <p>Provides a fluent API to define attendance properties, including project and formerStudent
 * associations, with sensible defaults for required metadata.
 */
public class AttendanceBuilder {
  private Project project = ProjectBuilder.aProject().build();
  private FormerStudent formerStudent = FormerStudentBuilder.aStudent().build();
  private Enrollment enrollment;
  private final BigDecimal duration = new BigDecimal("1.00");
  private final String pepper = "pepper-" + UuidCreator.getTimeOrderedEpoch();

  private AttendanceBuilder() {}

  /**
   * Initializes a new instance of the AttendanceBuilder.
   *
   * @return a new AttendanceBuilder instance
   */
  public static AttendanceBuilder anAttendance() {
    return new AttendanceBuilder();
  }

  /**
   * Sets the project associated with this attendance record.
   *
   * @param project the {@link Project} aggregate
   * @return this builder instance
   */
  public AttendanceBuilder withProject(Project project) {
    this.project = project;
    return this;
  }

  /**
   * Sets the formerStudent associated with this attendance record.
   *
   * @param formerStudent the {@link FormerStudent} aggregate
   * @return this builder instance
   */
  public AttendanceBuilder withStudent(FormerStudent formerStudent) {
    this.formerStudent = formerStudent;
    return this;
  }

  /**
   * Sets the enrollment associated with this attendance record.
   *
   * @param enrollment the {@link Enrollment} aggregate
   * @return this builder instance
   */
  public AttendanceBuilder withEnrollment(Enrollment enrollment) {
    this.enrollment = enrollment;
    return this;
  }

  /**
   * Constructs the {@link Attendance} aggregate using the current builder state.
   *
   * @return a configured {@link Attendance} instance
   */
  public Attendance build() {
    Enrollment resolvedEnrollment =
        enrollment != null ? enrollment : Enrollment.factory(formerStudent, project);
    return Attendance.factory(resolvedEnrollment, duration, pepper);
  }
}
