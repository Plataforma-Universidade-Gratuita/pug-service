package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseCreateRequest;
import br.org.catolicasc.pug.helpers.TestNameGenerator;

/**
 * Builder class for creating {@link AreaOfExpertiseCreateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, allowing tests to override only the fields
 * relevant to the scenario under test.
 */
public class AreaOfExpertiseCreateRequestBuilder {
  private String name = TestNameGenerator.generateRandomAreaOfExpertiseName();

  private AreaOfExpertiseCreateRequestBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link AreaOfExpertiseCreateRequestBuilder} instance
   */
  public static AreaOfExpertiseCreateRequestBuilder aAreaOfExpertiseCreateRequest() {
    return new AreaOfExpertiseCreateRequestBuilder();
  }

  /**
   * Sets the areaOfExpertise name.
   *
   * @param name the areaOfExpertise name
   * @return this builder instance
   */
  public AreaOfExpertiseCreateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link AreaOfExpertiseCreateRequest} using the current builder state.
   *
   * @return a configured {@link AreaOfExpertiseCreateRequest} instance
   */
  public AreaOfExpertiseCreateRequest build() {
    return new AreaOfExpertiseCreateRequest(name);
  }
}
