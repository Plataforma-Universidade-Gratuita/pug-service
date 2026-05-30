package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.academic.infra.read.AreasOfExpertiseQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.project.domain.ProjectSchoolRepository;
import br.org.catolicasc.pug.project.infra.read.ProjectQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.service.ProjectSchoolReadService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link ProjectSchoolReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying association
 * repository and the existing {@link AreasOfExpertiseQueries} and {@link ProjectQueries}
 * components. It resolves identifier sets via {@link ProjectSchoolRepository} and then projects
 * them into read models without instantiating full domain aggregates.
 */
@ApplicationScoped
public class ProjectSchoolReadServiceImpl implements ProjectSchoolReadService {

  private static final Logger LOG = Logger.getLogger(ProjectSchoolReadServiceImpl.class);

  @Inject ProjectSchoolRepository associationRepo;

  @Inject AreasOfExpertiseQueries areasOfExpertiseQueries;

  @Inject ProjectQueries projectQueries;

  /** {@inheritDoc} */
  @Override
  public Set<AreaOfExpertiseView> listAllAreasOfExpertiseByProjectId(UUID projectId) {
    if (projectId == null) {
      return Set.of();
    }

    var areaOfExpertiseIds = associationRepo.findAllSchoolIdsByProjectId(projectId);
    if (areaOfExpertiseIds.isEmpty()) {
      LOG.debugf(
          "No ProjectsBySchool associations found for projectId=%s when listing areas of expertise",
          projectId);
      return Set.of();
    }

    List<AreaOfExpertiseView> areasOfExpertise =
        areasOfExpertiseQueries.listAllByIds(List.copyOf(areaOfExpertiseIds));
    return new HashSet<>(areasOfExpertise);
  }

  /** {@inheritDoc} */
  @Override
  public Set<ProjectView> listAllProjectsByAreaOfExpertiseId(UUID areaOfExpertiseId) {
    if (areaOfExpertiseId == null) {
      return Set.of();
    }

    var projectIds = associationRepo.findAllProjectIdsBySchoolId(areaOfExpertiseId);
    if (projectIds.isEmpty()) {
      LOG.debugf(
          "No ProjectsBySchool associations found for areaOfExpertiseId=%s when listing projects",
          areaOfExpertiseId);
      return Set.of();
    }

    List<ProjectView> result = projectQueries.listAllByIds(List.copyOf(projectIds));
    return new HashSet<>(result);
  }
}
