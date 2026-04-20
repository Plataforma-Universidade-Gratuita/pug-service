package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.academic.infra.read.SchoolQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.project.domain.ProjectBySchoolRepository;
import br.org.catolicasc.pug.project.infra.read.ProjectQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.service.ProjectBySchoolReadService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link ProjectBySchoolReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying association
 * repository and the existing {@link SchoolQueries} and {@link ProjectQueries} components. It
 * resolves identifier sets via {@link ProjectBySchoolRepository} and then projects them into read
 * models without instantiating full domain aggregates.
 */
@ApplicationScoped
public class ProjectBySchoolReadServiceImpl implements ProjectBySchoolReadService {

  private static final Logger LOG = Logger.getLogger(ProjectBySchoolReadServiceImpl.class);

  @Inject ProjectBySchoolRepository associationRepo;

  @Inject SchoolQueries schoolQueries;

  @Inject ProjectQueries projectQueries;

  @Override
  public Set<SchoolView> listAllSchoolsByProjectId(UUID projectId) {
    if (projectId == null) {
      return Set.of();
    }

    var schoolIds = associationRepo.findAllSchoolIdsByProjectId(projectId);
    if (schoolIds.isEmpty()) {
      LOG.debugf(
          "No ProjectsBySchool associations found for projectId=%s when listing schools",
          projectId);
      return Set.of();
    }

    List<SchoolView> schools = schoolQueries.listByIds(List.copyOf(schoolIds));
    return new HashSet<>(schools);
  }

  @Override
  public Set<ProjectView> listAllProjectsBySchoolId(UUID schoolId) {
    if (schoolId == null) {
      return Set.of();
    }

    var projectIds = associationRepo.findAllProjectIdsBySchoolId(schoolId);
    if (projectIds.isEmpty()) {
      LOG.debugf(
          "No ProjectsBySchool associations found for schoolId=%s when listing projects", schoolId);
      return Set.of();
    }

    List<ProjectView> result = projectQueries.listByIds(List.copyOf(projectIds));
    return new HashSet<>(result);
  }
}
