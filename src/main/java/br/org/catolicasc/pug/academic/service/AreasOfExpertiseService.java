package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.service.dtos.AreaOfExpertiseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.AreaOfExpertiseUpdateCommand;
import java.util.UUID;

/** Command-side service for managing academic areas of expertise. */
public interface AreasOfExpertiseService {
  boolean delete(UUID id);

  School getById(UUID id);

  School save(AreaOfExpertiseCreateCommand cmd);

  School update(UUID id, AreaOfExpertiseUpdateCommand cmd);
}
