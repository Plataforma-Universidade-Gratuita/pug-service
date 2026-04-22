package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.academic.presenter.dtos.CourseCreateRequest;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

/**
 * Builder class for creating {@link CourseCreateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, allowing tests to override only the fields
 * relevant to the scenario under test.
 */
public class CourseCreateRequestBuilder {
  private String name = TestNameGenerator.generateRandomCourseName();
  private UUID schoolId = UuidCreator.getTimeOrderedEpoch();

  private CourseCreateRequestBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link CourseCreateRequestBuilder} instance
   */
  public static CourseCreateRequestBuilder aCourseCreateRequest() {
    return new CourseCreateRequestBuilder();
  }

  /**
   * Sets the course name.
   *
   * @param name the course name
   * @return this builder instance
   */
  public CourseCreateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the school identifier.
   *
   * @param schoolId the UUID of the parent school
   * @return this builder instance
   */
  public CourseCreateRequestBuilder withSchoolId(UUID schoolId) {
    this.schoolId = schoolId;
    return this;
  }

  /**
   * Constructs the {@link CourseCreateRequest} using the current builder state.
   *
   * @return a configured {@link CourseCreateRequest} instance
   */
  public CourseCreateRequest build() {
    return new CourseCreateRequest(name, schoolId);
  }
}
