package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceValidateRequest;

/**
 * Builder class for creating {@link AttendanceValidateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with sensible defaults for validation requests.
 */
public class AttendanceValidateRequestBuilder {
  private AttendanceStatus status = AttendanceStatus.PRESENT;
  private String qrValidationHash = "test-hash";

  private AttendanceValidateRequestBuilder() {}

  public static AttendanceValidateRequestBuilder anAttendanceValidateRequest() {
    return new AttendanceValidateRequestBuilder();
  }

  public AttendanceValidateRequestBuilder withStatus(AttendanceStatus status) {
    this.status = status;
    return this;
  }

  public AttendanceValidateRequestBuilder withQrValidationHash(String qrValidationHash) {
    this.qrValidationHash = qrValidationHash;
    return this;
  }

  public AttendanceValidateRequest build() {
    return new AttendanceValidateRequest(status, qrValidationHash);
  }
}
