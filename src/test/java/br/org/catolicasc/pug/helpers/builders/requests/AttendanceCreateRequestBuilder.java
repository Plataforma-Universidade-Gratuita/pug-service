package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.project.presenter.dtos.attendance.AttendanceCreateRequest;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Builder class for creating {@link AttendanceCreateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults for all fields.
 */
public class AttendanceCreateRequestBuilder {
  private UUID projectId = UuidCreator.getTimeOrderedEpoch();
  private UUID formerStudentId = UuidCreator.getTimeOrderedEpoch();
  private BigDecimal duration = new BigDecimal("2.0");

  private AttendanceCreateRequestBuilder() {}

  public static AttendanceCreateRequestBuilder anAttendanceCreateRequest() {
    return new AttendanceCreateRequestBuilder();
  }

  public AttendanceCreateRequestBuilder withProjectId(UUID projectId) {
    this.projectId = projectId;
    return this;
  }

  public AttendanceCreateRequestBuilder withStudentId(UUID formerStudentId) {
    this.formerStudentId = formerStudentId;
    return this;
  }

  public AttendanceCreateRequestBuilder withDuration(BigDecimal duration) {
    this.duration = duration;
    return this;
  }

  public AttendanceCreateRequest build() {
    return new AttendanceCreateRequest(projectId, formerStudentId, duration);
  }
}
