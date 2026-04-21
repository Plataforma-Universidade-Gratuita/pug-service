package br.org.catolicasc.pug.helpers.builders;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.Project;

/**
 * Builder class for creating {@link Enrollment} domain aggregates in test scenarios.
 *
 * <p>Provides a fluent API to initialize enrollment records between a student and a project,
 * utilizing default builders for the associated domain entities.
 */
public class EnrollmentBuilder {
  private final Student student = StudentBuilder.aStudent().build();
  private final Project project = ProjectBuilder.aProject().build();

  private EnrollmentBuilder() {}

  /**
   * Initializes a new instance of the EnrollmentBuilder.
   *
   * @return a new EnrollmentBuilder instance
   */
  public static EnrollmentBuilder anEnrollment() {
    return new EnrollmentBuilder();
  }

  /**
   * Constructs the {@link Enrollment} aggregate using the default student and project states.
   *
   * @return a configured {@link Enrollment} instance
   */
  public Enrollment build() {
    return Enrollment.factory(student, project);
  }
}
