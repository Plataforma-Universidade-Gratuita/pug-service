package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.partner.presenter.dtos.staff.StaffUpdateRequest;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

/**
 * Builder class for creating {@link StaffUpdateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with sensible defaults for partial updates.
 */
public class StaffUpdateRequestBuilder {
  private String name = TestNameGenerator.generateRandomName();
  private String emailString = null;
  private UUID entityId = UuidCreator.getTimeOrderedEpoch();

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

  public StaffUpdateRequestBuilder withEntityId(UUID entityId) {
    this.entityId = entityId;
    return this;
  }

  public StaffUpdateRequest build() {
    return new StaffUpdateRequest(name, emailString, entityId);
  }
}
