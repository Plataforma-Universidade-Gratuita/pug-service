package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.project.presenter.dtos.EnrollmentCreateRequest;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

/**
 * Builder class for creating {@link EnrollmentCreateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with a random default project ID.
 */
public class EnrollmentCreateRequestBuilder {
  private UUID projectId = UuidCreator.getTimeOrderedEpoch();

  private EnrollmentCreateRequestBuilder() {}

  public static EnrollmentCreateRequestBuilder anEnrollmentCreateRequest() {
    return new EnrollmentCreateRequestBuilder();
  }

  public EnrollmentCreateRequestBuilder withProjectId(UUID projectId) {
    this.projectId = projectId;
    return this;
  }

  public EnrollmentCreateRequest build() {
    return new EnrollmentCreateRequest(projectId);
  }
}
