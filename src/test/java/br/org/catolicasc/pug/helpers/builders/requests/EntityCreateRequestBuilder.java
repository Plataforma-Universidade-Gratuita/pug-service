package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.partner.presenter.dtos.EntityCreateRequest;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

/**
 * Builder class for creating {@link EntityCreateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, generating a valid CNPJ and unique entity name.
 */
public class EntityCreateRequestBuilder {
  private String cnpjString = TestBrazilianIdentifierGenerator.generateValidCnpj();
  private String name = TestNameGenerator.generateRandomEntityName();
  private UUID cityId = UuidCreator.getTimeOrderedEpoch();
  private String address = TestNameGenerator.generateUniqueAddress();

  private EntityCreateRequestBuilder() {}

  public static EntityCreateRequestBuilder anEntityCreateRequest() {
    return new EntityCreateRequestBuilder();
  }

  public EntityCreateRequestBuilder withCnpj(String cnpjString) {
    this.cnpjString = cnpjString;
    return this;
  }

  public EntityCreateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public EntityCreateRequestBuilder withCityId(UUID cityId) {
    this.cityId = cityId;
    return this;
  }

  public EntityCreateRequestBuilder withAddress(String address) {
    this.address = address;
    return this;
  }

  public EntityCreateRequest build() {
    return new EntityCreateRequest(cnpjString, name, cityId, address);
  }
}
