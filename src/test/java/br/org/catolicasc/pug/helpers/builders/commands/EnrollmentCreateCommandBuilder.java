package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentCreateCommand;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

/**
 * Builder class for creating {@link EnrollmentCreateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with a random default project ID.
 */
public class EnrollmentCreateCommandBuilder {
  private UUID projectId = UuidCreator.getTimeOrderedEpoch();
  private UUID formerStudentId;

  private EnrollmentCreateCommandBuilder() {}

  public static EnrollmentCreateCommandBuilder anEnrollmentCreateCommand() {
    return new EnrollmentCreateCommandBuilder();
  }

  public EnrollmentCreateCommandBuilder withProjectId(UUID projectId) {
    this.projectId = projectId;
    return this;
  }

  public EnrollmentCreateCommandBuilder withStudentId(UUID formerStudentId) {
    this.formerStudentId = formerStudentId;
    return this;
  }

  public EnrollmentCreateCommand build() {
    return new EnrollmentCreateCommand(projectId, formerStudentId);
  }
}
