package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.presenter.dtos.admins.AdminUpdateRequest;
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
  private Campi campus = getRandomCampus();

  private AdminUpdateRequestBuilder() {}

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

  public AdminUpdateRequestBuilder withCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  public AdminUpdateRequest build() {
    return new AdminUpdateRequest(name, emailString, campus);
  }

  private static Campi getRandomCampus() {
    Campi[] values = Campi.values();
    return values[new Random().nextInt(values.length)];
  }
}
