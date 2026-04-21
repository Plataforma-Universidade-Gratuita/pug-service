package br.org.catolicasc.pug.helpers.builders;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.Project;
import java.math.BigDecimal;

/**
 * Builder class for creating {@link Attendance} domain aggregates in test scenarios.
 *
 * <p>Provides a fluent API to define attendance properties, including project and student
 * associations, with sensible defaults for required metadata.
 */
public class AttendanceBuilder {
  private Project project = ProjectBuilder.aProject().build();
  private Student student = StudentBuilder.aStudent().build();
  private final BigDecimal duration = new BigDecimal("1.00");
  private final String hash = "hash-" + java.util.UUID.randomUUID();

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
   * Sets the student associated with this attendance record.
   *
   * @param student the {@link Student} aggregate
   * @return this builder instance
   */
  public AttendanceBuilder withStudent(Student student) {
    this.student = student;
    return this;
  }

  /**
   * Constructs the {@link Attendance} aggregate using the current builder state.
   *
   * @return a configured {@link Attendance} instance
   */
  public Attendance build() {
    return Attendance.factory(project, student, duration, hash);
  }
}
