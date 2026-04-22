package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.presenter.dtos.AdminCreateRequest;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.util.Random;

/**
 * Builder class for creating {@link AdminCreateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults for all fields, generating a valid CPF, unique
 * email, and sensible identity data.
 */
public class AdminCreateRequestBuilder {
  private String cpfString = TestBrazilianIdentifierGenerator.generateValidCpf();
  private String name = TestNameGenerator.generateRandomName();
  private String emailString = TestNameGenerator.generateUniqueEmail("pug.com");
  private String password = "password123";
  private Campi campus = getRandomCampus();

  private AdminCreateRequestBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link AdminCreateRequestBuilder} instance
   */
  public static AdminCreateRequestBuilder anAdminCreateRequest() {
    return new AdminCreateRequestBuilder();
  }

  public AdminCreateRequestBuilder withCpf(String cpfString) {
    this.cpfString = cpfString;
    return this;
  }

  public AdminCreateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public AdminCreateRequestBuilder withEmail(String emailString) {
    this.emailString = emailString;
    return this;
  }

  public AdminCreateRequestBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  /**
   * Sets the campus assignment.
   *
   * @param campus the {@link Campi} enum value
   * @return this builder instance
   */
  public AdminCreateRequestBuilder withCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  /**
   * Constructs the {@link AdminCreateRequest} using the current builder state.
   *
   * @return a configured {@link AdminCreateRequest} instance
   */
  public AdminCreateRequest build() {
    return new AdminCreateRequest(cpfString, name, emailString, password, campus);
  }

  private static Campi getRandomCampus() {
    Campi[] values = Campi.values();
    return values[new Random().nextInt(values.length)];
  }
}
