package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.partner.service.dtos.entities.EntityUpdateCommand;
import java.util.UUID;

/**
 * Builder class for creating {@link EntityUpdateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults for a full update simulation.
 */
public class EntityUpdateCommandBuilder {
  private String name = TestNameGenerator.generateRandomEntityName();
  private UUID cityId = null;
  private String address = null;

  private EntityUpdateCommandBuilder() {}

  public static EntityUpdateCommandBuilder anEntityUpdateCommand() {
    return new EntityUpdateCommandBuilder();
  }

  public EntityUpdateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public EntityUpdateCommandBuilder withCityId(UUID cityId) {
    this.cityId = cityId;
    return this;
  }

  public EntityUpdateCommandBuilder withAddress(String address) {
    this.address = address;
    return this;
  }

  public EntityUpdateCommand build() {
    return new EntityUpdateCommand(name, cityId, address);
  }
}
