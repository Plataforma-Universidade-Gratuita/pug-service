package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.helpers.TestNameGenerator;

/**
 * Builder class for creating {@link SchoolCreateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, allowing tests to override only the fields
 * relevant to the scenario under test.
 */
public class SchoolCreateRequestBuilder {
  private String name = TestNameGenerator.generateRandomSchoolName();

  private SchoolCreateRequestBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link SchoolCreateRequestBuilder} instance
   */
  public static SchoolCreateRequestBuilder aSchoolCreateRequest() {
    return new SchoolCreateRequestBuilder();
  }

  /**
   * Sets the school name.
   *
   * @param name the school name
   * @return this builder instance
   */
  public SchoolCreateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link SchoolCreateRequest} using the current builder state.
   *
   * @return a configured {@link SchoolCreateRequest} instance
   */
  public SchoolCreateRequest build() {
    return new SchoolCreateRequest(name);
  }
}
