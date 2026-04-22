package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.project.service.dtos.ProjectUpdateCommand;
import java.math.BigDecimal;

/**
 * Builder class for creating {@link ProjectUpdateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with null defaults (partial update semantics).
 */
public class ProjectUpdateCommandBuilder {
  private String name;
  private String description;
  private Integer maxParticipants;
  private BigDecimal offeredHours;

  private ProjectUpdateCommandBuilder() {}

  public static ProjectUpdateCommandBuilder aProjectUpdateCommand() {
    return new ProjectUpdateCommandBuilder();
  }

  public ProjectUpdateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public ProjectUpdateCommandBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public ProjectUpdateCommandBuilder withMaxParticipants(Integer maxParticipants) {
    this.maxParticipants = maxParticipants;
    return this;
  }

  public ProjectUpdateCommandBuilder withOfferedHours(BigDecimal offeredHours) {
    this.offeredHours = offeredHours;
    return this;
  }

  public ProjectUpdateCommand build() {
    return new ProjectUpdateCommand(name, description, maxParticipants, offeredHours);
  }
}
