package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.partner.presenter.dtos.StaffCreateRequest;
import java.util.UUID;

/**
 * Builder class for creating {@link StaffCreateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, generating a valid CPF and unique name.
 */
public class StaffCreateRequestBuilder {
  private String cpfString = TestBrazilianIdentifierGenerator.generateValidCpf();
  private String name = TestNameGenerator.generateRandomName();
  private String emailString = "test-" + UUID.randomUUID().toString().substring(0, 8) + "@pug.com";
  private String password = "password123";
  private UUID entityId = UUID.randomUUID();

  private StaffCreateRequestBuilder() {}

  public static StaffCreateRequestBuilder aStaffCreateRequest() {
    return new StaffCreateRequestBuilder();
  }

  public StaffCreateRequestBuilder withCpf(String cpfString) {
    this.cpfString = cpfString;
    return this;
  }

  public StaffCreateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public StaffCreateRequestBuilder withEmail(String emailString) {
    this.emailString = emailString;
    return this;
  }

  public StaffCreateRequestBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  public StaffCreateRequestBuilder withEntityId(UUID entityId) {
    this.entityId = entityId;
    return this;
  }

  public StaffCreateRequest build() {
    return new StaffCreateRequest(cpfString, name, emailString, password, entityId);
  }
}
