package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.helpers.TestNameGenerator;

/**
 * Builder class for creating {@link SchoolUpdateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults. Since update requests treat {@code null} fields as
 * "no change", the default populates all fields to simulate a full update.
 */
public class SchoolUpdateRequestBuilder {
  private String name = TestNameGenerator.generateRandomSchoolName();

  private SchoolUpdateRequestBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link SchoolUpdateRequestBuilder} instance
   */
  public static SchoolUpdateRequestBuilder aSchoolUpdateRequest() {
    return new SchoolUpdateRequestBuilder();
  }

  /**
   * Sets the school name.
   *
   * @param name the new school name, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public SchoolUpdateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link SchoolUpdateRequest} using the current builder state.
   *
   * @return a configured {@link SchoolUpdateRequest} instance
   */
  public SchoolUpdateRequest build() {
    return new SchoolUpdateRequest(name);
  }
}
