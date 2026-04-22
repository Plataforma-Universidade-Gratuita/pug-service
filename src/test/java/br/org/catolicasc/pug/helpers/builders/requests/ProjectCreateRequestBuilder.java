package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectCreateRequest;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Builder class for creating {@link ProjectCreateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults for all fields.
 */
public class ProjectCreateRequestBuilder {
  private String name = TestNameGenerator.generateRandomProjectName();
  private UUID entityId = UUID.randomUUID();
  private String description = "Test project description";
  private Integer maxParticipants = 15;
  private BigDecimal offeredHours = new BigDecimal("30.00");

  private ProjectCreateRequestBuilder() {}

  public static ProjectCreateRequestBuilder aProjectCreateRequest() {
    return new ProjectCreateRequestBuilder();
  }

  public ProjectCreateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public ProjectCreateRequestBuilder withEntityId(UUID entityId) {
    this.entityId = entityId;
    return this;
  }

  public ProjectCreateRequestBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public ProjectCreateRequestBuilder withMaxParticipants(Integer maxParticipants) {
    this.maxParticipants = maxParticipants;
    return this;
  }

  public ProjectCreateRequestBuilder withOfferedHours(BigDecimal offeredHours) {
    this.offeredHours = offeredHours;
    return this;
  }

  public ProjectCreateRequest build() {
    return new ProjectCreateRequest(name, entityId, description, maxParticipants, offeredHours);
  }
}
