package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for associating schools to an
 * existing Project.
 *
 * <p>The project identifier is supplied in the request path, so this payload contains only the
 * collection of school identifiers to associate.
 *
 * @param schoolIds the non-empty list of school identifiers to associate with the target project
 */
public record ProjectSchoolRequest(@NotEmpty List<@NotNull @UuidV7 UUID> schoolIds) {

  public ProjectSchoolRequest {
    schoolIds = (schoolIds != null) ? List.copyOf(schoolIds) : null;
  }

  @Override
  public List<UUID> schoolIds() {
    return schoolIds;
  }
}
