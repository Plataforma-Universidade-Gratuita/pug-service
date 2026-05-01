package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffUpdateRequest;

/**
 * Builder class for creating {@link StaffUpdateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with sensible defaults for partial updates.
 */
public class StaffUpdateRequestBuilder {
  private String name = TestNameGenerator.generateRandomName();
  private String emailString = null;
  private String password = null;
  private Boolean active = null;

  private StaffUpdateRequestBuilder() {}

  public static StaffUpdateRequestBuilder aStaffUpdateRequest() {
    return new StaffUpdateRequestBuilder();
  }

  public StaffUpdateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public StaffUpdateRequestBuilder withEmail(String emailString) {
    this.emailString = emailString;
    return this;
  }

  public StaffUpdateRequestBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  public StaffUpdateRequestBuilder withActive(Boolean active) {
    this.active = active;
    return this;
  }

  public StaffUpdateRequest build() {
    return new StaffUpdateRequest(name, emailString, password, active);
  }
}
