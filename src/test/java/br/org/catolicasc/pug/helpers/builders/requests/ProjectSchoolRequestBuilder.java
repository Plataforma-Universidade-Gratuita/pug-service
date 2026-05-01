package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.project.presenter.dtos.ProjectSchoolRequest;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.List;
import java.util.UUID;

/**
 * Builder class for creating {@link ProjectSchoolRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults for all fields.
 */
public class ProjectSchoolRequestBuilder {
  private List<UUID> schoolIds = List.of(UuidCreator.getTimeOrderedEpoch());

  private ProjectSchoolRequestBuilder() {}

  public static ProjectSchoolRequestBuilder aProjectSchoolRequest() {
    return new ProjectSchoolRequestBuilder();
  }

  public ProjectSchoolRequestBuilder withSchoolIds(List<UUID> schoolIds) {
    this.schoolIds = schoolIds;
    return this;
  }

  public ProjectSchoolRequest build() {
    return new ProjectSchoolRequest(schoolIds);
  }
}
