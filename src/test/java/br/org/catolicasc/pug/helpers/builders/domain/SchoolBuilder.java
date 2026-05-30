package br.org.catolicasc.pug.helpers.builders.domain;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.helpers.TestNameGenerator;

/**
 * Builder class for creating {@link School} domain aggregates in test scenarios.
 *
 * <p>Provides a fluent API to define areaOfExpertise properties, utilizing a random unique name generator by
 * default.
 */
public class SchoolBuilder {
  private String name = TestNameGenerator.generateRandomSchoolName();

  private SchoolBuilder() {}

  /**
   * Initializes a new instance of the SchoolBuilder.
   *
   * @return a new SchoolBuilder instance
   */
  public static SchoolBuilder aSchool() {
    return new SchoolBuilder();
  }

  /**
   * Sets the name of the academic areaOfExpertise.
   *
   * @param name the areaOfExpertise name
   * @return this builder instance
   */
  public SchoolBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link School} aggregate using the current builder state.
   *
   * @return a configured {@link School} instance
   */
  public School build() {
    return School.factory(name);
  }
}
