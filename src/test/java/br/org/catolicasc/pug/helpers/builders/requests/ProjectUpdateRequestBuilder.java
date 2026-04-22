package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.project.presenter.dtos.ProjectUpdateRequest;
import java.math.BigDecimal;

/**
 * Builder class for creating {@link ProjectUpdateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with null defaults (partial update semantics).
 */
public class ProjectUpdateRequestBuilder {
  private String name;
  private String description;
  private Integer maxParticipants;
  private BigDecimal offeredHours;

  private ProjectUpdateRequestBuilder() {}

  public static ProjectUpdateRequestBuilder aProjectUpdateRequest() {
    return new ProjectUpdateRequestBuilder();
  }

  public ProjectUpdateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public ProjectUpdateRequestBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public ProjectUpdateRequestBuilder withMaxParticipants(Integer maxParticipants) {
    this.maxParticipants = maxParticipants;
    return this;
  }

  public ProjectUpdateRequestBuilder withOfferedHours(BigDecimal offeredHours) {
    this.offeredHours = offeredHours;
    return this;
  }

  public ProjectUpdateRequest build() {
    return new ProjectUpdateRequest(name, description, maxParticipants, offeredHours);
  }
}
