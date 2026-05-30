package br.org.catolicasc.pug.helpers.builders.domain;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.helpers.TestNameGenerator;

/**
 * Builder class for creating {@link AreaOfExpertise} domain aggregates in test scenarios.
 *
 * <p>Provides a fluent API to define areaOfExpertise properties, utilizing a random unique name
 * generator by default.
 */
public class AreaOfExpertiseBuilder {
  private String name = TestNameGenerator.generateRandomAreaOfExpertiseName();

  private AreaOfExpertiseBuilder() {}

  /**
   * Initializes a new instance of the AreaOfExpertiseBuilder.
   *
   * @return a new AreaOfExpertiseBuilder instance
   */
  public static AreaOfExpertiseBuilder aAreaOfExpertise() {
    return new AreaOfExpertiseBuilder();
  }

  /**
   * Sets the name of the academic areaOfExpertise.
   *
   * @param name the areaOfExpertise name
   * @return this builder instance
   */
  public AreaOfExpertiseBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link AreaOfExpertise} aggregate using the current builder state.
   *
   * @return a configured {@link AreaOfExpertise} instance
   */
  public AreaOfExpertise build() {
    return AreaOfExpertise.factory(name);
  }
}
