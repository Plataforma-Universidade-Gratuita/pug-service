package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.academic.service.dtos.SchoolUpdateCommand;
import br.org.catolicasc.pug.helpers.TestNameGenerator;

/**
 * Builder class for creating {@link SchoolUpdateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults. Since update commands treat {@code null} fields as
 * "no change", the default populates all fields to simulate a full update.
 */
public class SchoolUpdateCommandBuilder {
  private String name = TestNameGenerator.generateRandomSchoolName();

  private SchoolUpdateCommandBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link SchoolUpdateCommandBuilder} instance
   */
  public static SchoolUpdateCommandBuilder aSchoolUpdateCommand() {
    return new SchoolUpdateCommandBuilder();
  }

  /**
   * Sets the areaOfExpertise name.
   *
   * @param name the new areaOfExpertise name, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public SchoolUpdateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link SchoolUpdateCommand} using the current builder state.
   *
   * @return a configured {@link SchoolUpdateCommand} instance
   */
  public SchoolUpdateCommand build() {
    return new SchoolUpdateCommand(name);
  }
}
