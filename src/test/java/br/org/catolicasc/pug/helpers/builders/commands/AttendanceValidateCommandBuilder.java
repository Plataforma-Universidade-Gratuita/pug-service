package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.service.dtos.AttendanceValidateCommand;

/**
 * Builder class for creating {@link AttendanceValidateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with sensible defaults for validation commands.
 */
public class AttendanceValidateCommandBuilder {
  private String qrValidationHash = "test-hash";
  private AttendanceStatus status = AttendanceStatus.PRESENT;

  private AttendanceValidateCommandBuilder() {}

  public static AttendanceValidateCommandBuilder anAttendanceValidateCommand() {
    return new AttendanceValidateCommandBuilder();
  }

  public AttendanceValidateCommandBuilder withQrValidationHash(String qrValidationHash) {
    this.qrValidationHash = qrValidationHash;
    return this;
  }

  public AttendanceValidateCommandBuilder withStatus(AttendanceStatus status) {
    this.status = status;
    return this;
  }

  public AttendanceValidateCommand build() {
    return new AttendanceValidateCommand(qrValidationHash, status);
  }
}
