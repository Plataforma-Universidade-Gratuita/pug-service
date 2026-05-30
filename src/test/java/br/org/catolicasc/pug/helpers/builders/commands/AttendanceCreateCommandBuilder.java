package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.project.service.dtos.AttendanceCreateCommand;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Builder class for creating {@link AttendanceCreateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults for all fields.
 */
public class AttendanceCreateCommandBuilder {
  private UUID projectId = UuidCreator.getTimeOrderedEpoch();
  private UUID formerStudentId = UuidCreator.getTimeOrderedEpoch();
  private BigDecimal duration = new BigDecimal("2.0");

  private AttendanceCreateCommandBuilder() {}

  public static AttendanceCreateCommandBuilder anAttendanceCreateCommand() {
    return new AttendanceCreateCommandBuilder();
  }

  public AttendanceCreateCommandBuilder withProjectId(UUID projectId) {
    this.projectId = projectId;
    return this;
  }

  public AttendanceCreateCommandBuilder withStudentId(UUID formerStudentId) {
    this.formerStudentId = formerStudentId;
    return this;
  }

  public AttendanceCreateCommandBuilder withDuration(BigDecimal duration) {
    this.duration = duration;
    return this;
  }

  public AttendanceCreateCommand build() {
    return new AttendanceCreateCommand(projectId, formerStudentId, duration);
  }
}
