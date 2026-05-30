package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseUpdateRequest;
import br.org.catolicasc.pug.helpers.TestNameGenerator;

/**
 * Builder class for creating {@link AreaOfExpertiseUpdateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults. Since update requests treat {@code null} fields as
 * "no change", the default populates all fields to simulate a full update.
 */
public class AreaOfExpertiseUpdateRequestBuilder {
  private String name = TestNameGenerator.generateRandomAreaOfExpertiseName();

  private AreaOfExpertiseUpdateRequestBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link AreaOfExpertiseUpdateRequestBuilder} instance
   */
  public static AreaOfExpertiseUpdateRequestBuilder aAreaOfExpertiseUpdateRequest() {
    return new AreaOfExpertiseUpdateRequestBuilder();
  }

  /**
   * Sets the areaOfExpertise name.
   *
   * @param name the new areaOfExpertise name, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public AreaOfExpertiseUpdateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link AreaOfExpertiseUpdateRequest} using the current builder state.
   *
   * @return a configured {@link AreaOfExpertiseUpdateRequest} instance
   */
  public AreaOfExpertiseUpdateRequest build() {
    return new AreaOfExpertiseUpdateRequest(name);
  }
}
