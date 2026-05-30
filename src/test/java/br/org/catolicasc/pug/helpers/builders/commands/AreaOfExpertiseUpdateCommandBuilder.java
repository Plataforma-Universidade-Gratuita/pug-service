package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseUpdateCommand;
import br.org.catolicasc.pug.helpers.TestNameGenerator;

/**
 * Builder class for creating {@link AreaOfExpertiseUpdateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults. Since update commands treat {@code null} fields as
 * "no change", the default populates all fields to simulate a full update.
 */
public class AreaOfExpertiseUpdateCommandBuilder {
  private String name = TestNameGenerator.generateRandomAreaOfExpertiseName();

  private AreaOfExpertiseUpdateCommandBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link AreaOfExpertiseUpdateCommandBuilder} instance
   */
  public static AreaOfExpertiseUpdateCommandBuilder aAreaOfExpertiseUpdateCommand() {
    return new AreaOfExpertiseUpdateCommandBuilder();
  }

  /**
   * Sets the areaOfExpertise name.
   *
   * @param name the new areaOfExpertise name, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public AreaOfExpertiseUpdateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link AreaOfExpertiseUpdateCommand} using the current builder state.
   *
   * @return a configured {@link AreaOfExpertiseUpdateCommand} instance
   */
  public AreaOfExpertiseUpdateCommand build() {
    return new AreaOfExpertiseUpdateCommand(name);
  }
}
