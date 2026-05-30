package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.academic.service.dtos.courses.CourseUpdateCommand;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

/**
 * Builder class for creating {@link CourseUpdateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults. Since update commands treat {@code null} fields as
 * "no change", the default populates all fields to simulate a full update.
 */
public class CourseUpdateCommandBuilder {
  private String name = TestNameGenerator.generateRandomCourseName();
  private UUID areaOfExpertiseId = UuidCreator.getTimeOrderedEpoch();

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
   * Sets the areaOfExpertise identifier.
   *
   * @param areaOfExpertiseId the new areaOfExpertise UUID, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public CourseUpdateCommandBuilder withAreaOfExpertiseId(UUID areaOfExpertiseId) {
    this.areaOfExpertiseId = areaOfExpertiseId;
    return this;
  }

  /**
   * Constructs the {@link CourseUpdateCommand} using the current builder state.
   *
   * @return a configured {@link CourseUpdateCommand} instance
   */
  public CourseUpdateCommand build() {
    return new CourseUpdateCommand(name, areaOfExpertiseId);
  }
}
