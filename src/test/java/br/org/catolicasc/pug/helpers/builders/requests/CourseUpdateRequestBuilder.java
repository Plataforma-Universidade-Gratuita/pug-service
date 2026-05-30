package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseUpdateRequest;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

/**
 * Builder class for creating {@link CourseUpdateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults. Since update requests treat {@code null} fields as
 * "no change", the default populates all fields to simulate a full update.
 */
public class CourseUpdateRequestBuilder {
  private String name = TestNameGenerator.generateRandomCourseName();
  private UUID areaOfExpertiseId = UuidCreator.getTimeOrderedEpoch();

  private CourseUpdateRequestBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link CourseUpdateRequestBuilder} instance
   */
  public static CourseUpdateRequestBuilder aCourseUpdateRequest() {
    return new CourseUpdateRequestBuilder();
  }

  /**
   * Sets the course name.
   *
   * @param name the new course name, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public CourseUpdateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the areaOfExpertise identifier.
   *
   * @param areaOfExpertiseId the new areaOfExpertise UUID, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public CourseUpdateRequestBuilder withAreaOfExpertiseId(UUID areaOfExpertiseId) {
    this.areaOfExpertiseId = areaOfExpertiseId;
    return this;
  }

  /**
   * Constructs the {@link CourseUpdateRequest} using the current builder state.
   *
   * @return a configured {@link CourseUpdateRequest} instance
   */
  public CourseUpdateRequest build() {
    return new CourseUpdateRequest(name, areaOfExpertiseId);
  }
}
