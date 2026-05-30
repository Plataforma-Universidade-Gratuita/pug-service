package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectAreaOfExpertiseRequest;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.List;
import java.util.UUID;

/**
 * Builder class for creating {@link ProjectAreaOfExpertiseRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults for all fields.
 */
public class ProjectAreaOfExpertiseRequestBuilder {
  private List<UUID> areaOfExpertiseIds = List.of(UuidCreator.getTimeOrderedEpoch());

  private ProjectAreaOfExpertiseRequestBuilder() {}

  public static ProjectAreaOfExpertiseRequestBuilder aProjectAreaOfExpertiseRequest() {
    return new ProjectAreaOfExpertiseRequestBuilder();
  }

  public ProjectAreaOfExpertiseRequestBuilder withAreaOfExpertiseIds(
      List<UUID> areaOfExpertiseIds) {
    this.areaOfExpertiseIds = areaOfExpertiseIds;
    return this;
  }

  public ProjectAreaOfExpertiseRequest build() {
    return new ProjectAreaOfExpertiseRequest(areaOfExpertiseIds);
  }
}
