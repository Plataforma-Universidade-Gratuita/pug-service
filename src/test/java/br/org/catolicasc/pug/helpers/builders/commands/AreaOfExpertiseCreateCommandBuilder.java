package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseCreateCommand;
import br.org.catolicasc.pug.helpers.TestNameGenerator;

/**
 * Builder class for creating {@link AreaOfExpertiseCreateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, allowing tests to override only the fields
 * relevant to the scenario under test.
 */
public class AreaOfExpertiseCreateCommandBuilder {
  private String name = TestNameGenerator.generateRandomAreaOfExpertiseName();

  private AreaOfExpertiseCreateCommandBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link AreaOfExpertiseCreateCommandBuilder} instance
   */
  public static AreaOfExpertiseCreateCommandBuilder aAreaOfExpertiseCreateCommand() {
    return new AreaOfExpertiseCreateCommandBuilder();
  }

  /**
   * Sets the areaOfExpertise name.
   *
   * @param name the areaOfExpertise name
   * @return this builder instance
   */
  public AreaOfExpertiseCreateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link AreaOfExpertiseCreateCommand} using the current builder state.
   *
   * @return a configured {@link AreaOfExpertiseCreateCommand} instance
   */
  public AreaOfExpertiseCreateCommand build() {
    return new AreaOfExpertiseCreateCommand(name);
  }
}
