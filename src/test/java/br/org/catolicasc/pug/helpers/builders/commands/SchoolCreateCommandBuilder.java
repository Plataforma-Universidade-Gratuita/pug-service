package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.academic.service.dtos.SchoolCreateCommand;
import br.org.catolicasc.pug.helpers.TestNameGenerator;

/**
 * Builder class for creating {@link SchoolCreateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, allowing tests to override only the fields
 * relevant to the scenario under test.
 */
public class SchoolCreateCommandBuilder {
  private String name = TestNameGenerator.generateRandomSchoolName();

  private SchoolCreateCommandBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link SchoolCreateCommandBuilder} instance
   */
  public static SchoolCreateCommandBuilder aSchoolCreateCommand() {
    return new SchoolCreateCommandBuilder();
  }

  /**
   * Sets the school name.
   *
   * @param name the school name
   * @return this builder instance
   */
  public SchoolCreateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link SchoolCreateCommand} using the current builder state.
   *
   * @return a configured {@link SchoolCreateCommand} instance
   */
  public SchoolCreateCommand build() {
    return new SchoolCreateCommand(name);
  }
}
