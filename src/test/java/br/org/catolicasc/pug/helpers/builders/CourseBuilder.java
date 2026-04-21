package br.org.catolicasc.pug.helpers.builders;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import java.util.UUID;

/**
 * Builder class for creating {@link Course} domain aggregates in test scenarios.
 *
 * <p>Provides a fluent API to define course properties, using a random unique name generator by
 * default to avoid database constraint violations.
 */
public class CourseBuilder {
  private String name = TestNameGenerator.generateRandomCourseName();
  private UUID schoolId = UUID.randomUUID();

  private CourseBuilder() {}

  /**
   * Initializes a new instance of the CourseBuilder.
   *
   * @return a new CourseBuilder instance
   */
  public static CourseBuilder aCourse() {
    return new CourseBuilder();
  }

  /**
   * Sets the name of the academic course.
   *
   * @param name the course name
   * @return this builder instance
   */
  public CourseBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the school identifier linked to this course.
   *
   * @param schoolId the UUID of the parent {@link br.org.catolicasc.pug.academic.domain.School}
   * @return this builder instance
   */
  public CourseBuilder withSchool(UUID schoolId) {
    this.schoolId = schoolId;
    return this;
  }

  /**
   * Constructs the {@link Course} aggregate using the current builder state.
   *
   * @return a configured {@link Course} instance
   */
  public Course build() {
    return Course.factory(name, schoolId);
  }
}
