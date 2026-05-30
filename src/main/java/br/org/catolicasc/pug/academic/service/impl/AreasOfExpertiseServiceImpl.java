package br.org.catolicasc.pug.academic.service.impl;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.service.AreasOfExpertiseService;
import br.org.catolicasc.pug.academic.service.SchoolService;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseUpdateCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;

/** Adapter that exposes the school command flow under the public area-of-expertise nomenclature. */
@ApplicationScoped
public class AreasOfExpertiseServiceImpl implements AreasOfExpertiseService {

  @Inject SchoolService delegate;

  @Override
  public boolean delete(UUID id) {
    return delegate.delete(id);
  }

  @Override
  public School getById(UUID id) {
    return delegate.getById(id);
  }

  @Override
  public School save(AreaOfExpertiseCreateCommand cmd) {
    return delegate.save(
        new br.org.catolicasc.pug.academic.service.dtos.SchoolCreateCommand(cmd.name()));
  }

  @Override
  public School update(UUID id, AreaOfExpertiseUpdateCommand cmd) {
    return delegate.update(
        id, new br.org.catolicasc.pug.academic.service.dtos.SchoolUpdateCommand(cmd.name()));
  }
}
