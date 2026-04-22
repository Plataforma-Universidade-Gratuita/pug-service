package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.academic.service.dtos.CourseUpdateCommand;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import java.util.UUID;

/**
 * Builder class for creating {@link CourseUpdateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults. Since update commands treat {@code null} fields as
 * "no change", the default populates all fields to simulate a full update.
 */
public class CourseUpdateCommandBuilder {
  private String name = TestNameGenerator.generateRandomCourseName();
  private UUID schoolId = UUID.randomUUID();

  private CourseUpdateCommandBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link CourseUpdateCommandBuilder} instance
   */
  public static CourseUpdateCommandBuilder aCourseUpdateCommand() {
    return new CourseUpdateCommandBuilder();
  }

  /**
   * Sets the course name.
   *
   * @param name the new course name, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public CourseUpdateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the school identifier.
   *
   * @param schoolId the new school UUID, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public CourseUpdateCommandBuilder withSchoolId(UUID schoolId) {
    this.schoolId = schoolId;
    return this;
  }

  /**
   * Constructs the {@link CourseUpdateCommand} using the current builder state.
   *
   * @return a configured {@link CourseUpdateCommand} instance
   */
  public CourseUpdateCommand build() {
    return new CourseUpdateCommand(name, schoolId);
  }
}
