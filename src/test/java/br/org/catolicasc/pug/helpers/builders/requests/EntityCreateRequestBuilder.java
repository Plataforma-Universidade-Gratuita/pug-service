package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.partner.presenter.dtos.EntityCreateRequest;
import java.util.UUID;

/**
 * Builder class for creating {@link EntityCreateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, generating a valid CNPJ and unique entity name.
 */
public class EntityCreateRequestBuilder {
  private String cnpjString = TestBrazilianIdentifierGenerator.generateValidCnpj();
  private String name = TestNameGenerator.generateRandomEntityName();
  private UUID cityId = UUID.randomUUID();
  private String address = "Rua Test " + UUID.randomUUID().toString().substring(0, 6) + ", 123";

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
