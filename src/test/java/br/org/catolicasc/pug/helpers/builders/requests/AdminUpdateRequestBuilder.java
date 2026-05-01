package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.presenter.dtos.AdminUpdateRequest;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.util.Random;

/**
 * Builder class for creating {@link AdminUpdateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with sensible defaults for partial updates.
 */
public class AdminUpdateRequestBuilder {
  private String name = TestNameGenerator.generateRandomName();
  private String emailString = null;
  private String password = null;
  private Campi campus = getRandomCampus();
  private Boolean active = null;

  private AdminUpdateRequestBuilder() {}

  /**
   * Initializes a new builder with sensible defaults for a partial update.
   *
   * @return a new {@link AdminUpdateRequestBuilder} instance
   */
  public static AdminUpdateRequestBuilder anAdminUpdateRequest() {
    return new AdminUpdateRequestBuilder();
  }

  public AdminUpdateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public AdminUpdateRequestBuilder withEmail(String emailString) {
    this.emailString = emailString;
    return this;
  }

  public AdminUpdateRequestBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  /**
   * Sets the campus assignment.
   *
   * @param campus the new {@link Campi}, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public AdminUpdateRequestBuilder withCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  public AdminUpdateRequestBuilder withActive(Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Constructs the {@link AdminUpdateRequest} using the current builder state.
   *
   * @return a configured {@link AdminUpdateRequest} instance
   */
  public AdminUpdateRequest build() {
    return new AdminUpdateRequest(name, emailString, password, campus, active);
  }

  private static Campi getRandomCampus() {
    Campi[] values = Campi.values();
    return values[new Random().nextInt(values.length)];
  }
}
