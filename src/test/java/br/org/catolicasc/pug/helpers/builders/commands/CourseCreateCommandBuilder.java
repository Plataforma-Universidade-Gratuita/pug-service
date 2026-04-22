package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.academic.service.dtos.CourseCreateCommand;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import java.util.UUID;

/**
 * Builder class for creating {@link CourseCreateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, allowing tests to override only the fields
 * relevant to the scenario under test.
 */
public class CourseCreateCommandBuilder {
  private String name = TestNameGenerator.generateRandomCourseName();
  private UUID schoolId = UUID.randomUUID();

  private CourseCreateCommandBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link CourseCreateCommandBuilder} instance
   */
  public static CourseCreateCommandBuilder aCourseCreateCommand() {
    return new CourseCreateCommandBuilder();
  }

  /**
   * Sets the course name.
   *
   * @param name the course name
   * @return this builder instance
   */
  public CourseCreateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the school identifier.
   *
   * @param schoolId the UUID of the parent school
   * @return this builder instance
   */
  public CourseCreateCommandBuilder withSchoolId(UUID schoolId) {
    this.schoolId = schoolId;
    return this;
  }

  /**
   * Constructs the {@link CourseCreateCommand} using the current builder state.
   *
   * @return a configured {@link CourseCreateCommand} instance
   */
  public CourseCreateCommand build() {
    return new CourseCreateCommand(name, schoolId);
  }
}
