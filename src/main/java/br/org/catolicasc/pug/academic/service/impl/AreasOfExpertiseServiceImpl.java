package br.org.catolicasc.pug.academic.service.impl;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.AreaOfExpertiseRepository;
import br.org.catolicasc.pug.academic.service.AreasOfExpertiseService;
import br.org.catolicasc.pug.academic.service.CoursesService;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseUpdateCommand;
import br.org.catolicasc.pug.academic.service.utils.AreaOfExpertiseProcessor;
import br.org.catolicasc.pug.academic.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.project.service.ProjectSchoolService;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Command-side implementation for academic areas of expertise. */
@ApplicationScoped
public class AreasOfExpertiseServiceImpl implements AreasOfExpertiseService {

  private static final Logger LOG = Logger.getLogger(AreasOfExpertiseServiceImpl.class);

  @Inject AuditPublisher auditPublisher;
  @Inject AreaOfExpertiseRepository repo;
  @Inject CoursesService coursesService;
  @Inject ProjectSchoolService projectSchoolService;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete AreaOfExpertise ID: %s", id);
    if (id == null) {
      return false;
    }

    if (coursesService.existsAnyByAreaOfExpertiseId(id)) {
      LOG.warnf("Delete failed: AreaOfExpertise ID %s has active courses", id);
      throw ExceptionHelper.schoolHasCourses();
    }

    boolean deleted = repo.deleteById(id);
    if (deleted) {
      LOG.infof("AreaOfExpertise deleted successfully. ID: %s", id);
      projectSchoolService.deleteAllByAreaOfExpertiseId(id);
      auditPublisher.fireDelete(AreaOfExpertise.class.getName(), id);
    } else {
      LOG.debugf("Delete failed: AreaOfExpertise ID %s not found (idempotent)", id);
    }

    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public AreaOfExpertise getById(UUID id) {
    AreaOfExpertise areaOfExpertise =
        repo.findOptionalById(id)
            .orElseThrow(
                () -> {
                  LOG.debugf("AreaOfExpertise lookup failed: ID %s not found", id);
                  return ExceptionHelper.schoolNotFound();
                });

    if (areaOfExpertise.hasFieldErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: AreaOfExpertise %s violates domain rules: %s",
          id, areaOfExpertise.getProblemsSummary());
      throw ExceptionHelper.schoolNotFound();
    }
    return areaOfExpertise;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public AreaOfExpertise save(AreaOfExpertiseCreateCommand cmd) {
    LOG.debugf("Attempting to create AreaOfExpertise: %s", cmd.name());
    AreaOfExpertise areaOfExpertiseToPersist =
        AreaOfExpertiseProcessor.processCreateInput(cmd.name());

    if (areaOfExpertiseToPersist.hasFieldErrors()) {
      throw new AppValidationException(areaOfExpertiseToPersist.getFieldErrors());
    }

    if (existsByName(areaOfExpertiseToPersist.getName())) {
      LOG.warnf(
          "Creation failed: AreaOfExpertise with name %s already exists",
          areaOfExpertiseToPersist.getName());
      throw ExceptionHelper.schoolAlreadyExists();
    }

    AreaOfExpertise savedAreaOfExpertise = repo.persist(areaOfExpertiseToPersist);
    LOG.infof("AreaOfExpertise created successfully. ID: %s", savedAreaOfExpertise.getId());

    auditPublisher.fireCreate(AreaOfExpertise.class.getName(), savedAreaOfExpertise.getId());
    return savedAreaOfExpertise;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public AreaOfExpertise update(UUID id, AreaOfExpertiseUpdateCommand cmd) {
    LOG.debugf("Attempting to update AreaOfExpertise ID: %s", id);
    AreaOfExpertise current = getById(id);
    AreaOfExpertise updatedAreaOfExpertise =
        AreaOfExpertiseProcessor.processUpdateInput(current, cmd.name());

    if (updatedAreaOfExpertise.hasFieldErrors()) {
      throw new AppValidationException(updatedAreaOfExpertise.getFieldErrors());
    }

    if (!updatedAreaOfExpertise.getName().equals(current.getName())
        && existsByName(updatedAreaOfExpertise.getName())) {
      LOG.warnf(
          "Update failed: AreaOfExpertise ID %s tried to use existing name %s",
          id, updatedAreaOfExpertise.getName());
      throw ExceptionHelper.schoolAlreadyExists();
    }

    repo.update(updatedAreaOfExpertise);
    LOG.infof("AreaOfExpertise updated successfully. ID: %s", id);

    auditPublisher.fireUpdate(AreaOfExpertise.class.getName(), id, current, updatedAreaOfExpertise);
    return getById(id);
  }

  private boolean existsByName(String name) {
    if (StringUtils.isEmpty(name)) {
      return false;
    }
    return repo.existsByName(name);
  }
}
