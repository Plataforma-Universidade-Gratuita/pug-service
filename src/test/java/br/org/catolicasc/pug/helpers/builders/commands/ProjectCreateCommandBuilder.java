package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.project.service.dtos.ProjectCreateCommand;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Builder class for creating {@link ProjectCreateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults for all fields.
 */
public class ProjectCreateCommandBuilder {
  private String name = TestNameGenerator.generateRandomProjectName();
  private UUID entityId = UUID.randomUUID();
  private String description = "Test project description";
  private Integer maxParticipants = 20;
  private BigDecimal offeredHours = new BigDecimal("40.00");

  private ProjectCreateCommandBuilder() {}

  public static ProjectCreateCommandBuilder aProjectCreateCommand() {
    return new ProjectCreateCommandBuilder();
  }

  public ProjectCreateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public ProjectCreateCommandBuilder withEntityId(UUID entityId) {
    this.entityId = entityId;
    return this;
  }

  public ProjectCreateCommandBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public ProjectCreateCommandBuilder withMaxParticipants(Integer maxParticipants) {
    this.maxParticipants = maxParticipants;
    return this;
  }

  public ProjectCreateCommandBuilder withOfferedHours(BigDecimal offeredHours) {
    this.offeredHours = offeredHours;
    return this;
  }

  public ProjectCreateCommand build() {
    return new ProjectCreateCommand(name, entityId, description, maxParticipants, offeredHours);
  }
}
