package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.partner.presenter.dtos.entities.EntityUpdateRequest;
import java.util.UUID;

/**
 * Builder class for creating {@link EntityUpdateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with sensible defaults for partial updates.
 */
public class EntityUpdateRequestBuilder {
  private String name = TestNameGenerator.generateRandomEntityName();
  private UUID cityId = null;
  private String address = null;

  private EntityUpdateRequestBuilder() {}

  public static EntityUpdateRequestBuilder anEntityUpdateRequest() {
    return new EntityUpdateRequestBuilder();
  }

  public EntityUpdateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public EntityUpdateRequestBuilder withCityId(UUID cityId) {
    this.cityId = cityId;
    return this;
  }

  public EntityUpdateRequestBuilder withAddress(String address) {
    this.address = address;
    return this;
  }

  public EntityUpdateRequest build() {
    return new EntityUpdateRequest(name, cityId, address);
  }
}
