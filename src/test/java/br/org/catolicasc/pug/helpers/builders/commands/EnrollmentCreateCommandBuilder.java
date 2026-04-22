package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.project.service.dtos.EnrollmentCreateCommand;
import java.util.UUID;

/**
 * Builder class for creating {@link EnrollmentCreateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with a random default project ID.
 */
public class EnrollmentCreateCommandBuilder {
  private UUID projectId = UUID.randomUUID();

  private EnrollmentCreateCommandBuilder() {}

  public static EnrollmentCreateCommandBuilder anEnrollmentCreateCommand() {
    return new EnrollmentCreateCommandBuilder();
  }

  public EnrollmentCreateCommandBuilder withProjectId(UUID projectId) {
    this.projectId = projectId;
    return this;
  }

  public EnrollmentCreateCommand build() {
    return new EnrollmentCreateCommand(projectId);
  }
}
