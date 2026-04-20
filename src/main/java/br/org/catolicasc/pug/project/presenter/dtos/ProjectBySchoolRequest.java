package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.ProjectBySchool;
import br.org.catolicasc.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for assigning a Project to one or
 * more Schools.
 *
 * <p>This request does not create or update the {@link Project} itself; it is dedicated exclusively
 * to managing the project–school association via {@link ProjectBySchool}.
 *
 * @param projectId the unique identifier (UUIDv7) of the project to be associated
 * @param schoolIds the list of unique identifiers (UUIDv7) of the schools to link to the project
 */
public record ProjectBySchoolRequest(
    @NotNull @UuidV7 UUID projectId, @NotEmpty List<@NotNull @UuidV7 UUID> schoolIds) {

  public ProjectBySchoolRequest {
    schoolIds = (schoolIds != null) ? List.copyOf(schoolIds) : null;
  }

  @Override
  public List<UUID> schoolIds() {
    return schoolIds;
  }
}
