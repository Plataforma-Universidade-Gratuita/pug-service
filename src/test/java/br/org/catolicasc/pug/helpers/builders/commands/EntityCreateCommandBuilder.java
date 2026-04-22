package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.partner.service.dtos.EntityCreateCommand;
import java.util.UUID;

/**
 * Builder class for creating {@link EntityCreateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults, generating a valid CNPJ and unique entity name.
 */
public class EntityCreateCommandBuilder {
  private String cnpjString = TestBrazilianIdentifierGenerator.generateValidCnpj();
  private String name = TestNameGenerator.generateRandomEntityName();
  private UUID cityId = UUID.randomUUID();
  private String address = "Rua Test " + UUID.randomUUID().toString().substring(0, 6) + ", 123";

  private EntityCreateCommandBuilder() {}

  public static EntityCreateCommandBuilder anEntityCreateCommand() {
    return new EntityCreateCommandBuilder();
  }

  public EntityCreateCommandBuilder withCnpj(String cnpjString) {
    this.cnpjString = cnpjString;
    return this;
  }

  public EntityCreateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public EntityCreateCommandBuilder withCityId(UUID cityId) {
    this.cityId = cityId;
    return this;
  }

  public EntityCreateCommandBuilder withAddress(String address) {
    this.address = address;
    return this;
  }

  public EntityCreateCommand build() {
    return new EntityCreateCommand(cnpjString, name, cityId, address);
  }
}
