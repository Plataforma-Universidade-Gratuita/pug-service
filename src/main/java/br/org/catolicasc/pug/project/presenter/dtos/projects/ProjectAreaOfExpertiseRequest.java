/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.presenter.dtos.projects;

import br.org.catolicasc.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for associating academic areas of
 * expertise to an existing project.
 *
 * <p>The project identifier is supplied in the request path, so this payload contains only the
 * collection of academic area-of-expertise identifiers to associate.
 *
 * @param areaOfExpertiseIds the non-empty list of area-of-expertise identifiers to associate with
 *     the target project
 */
public record ProjectAreaOfExpertiseRequest(
    @NotEmpty List<@NotNull @UuidV7 UUID> areaOfExpertiseIds) {

  public ProjectAreaOfExpertiseRequest {
    areaOfExpertiseIds = areaOfExpertiseIds == null ? null : List.copyOf(areaOfExpertiseIds);
  }

  @Override
  public List<UUID> areaOfExpertiseIds() {
    return areaOfExpertiseIds;
  }
}
