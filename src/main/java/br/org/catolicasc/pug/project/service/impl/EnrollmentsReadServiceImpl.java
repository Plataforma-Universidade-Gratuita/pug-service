package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.project.infra.read.EnrollmentsQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.service.EnrollmentsReadService;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentComplexSearchCriteria;
import br.org.catolicasc.pug.project.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Default application service that exposes the enrollment read-side use cases. */
@ApplicationScoped
public class EnrollmentsReadServiceImpl implements EnrollmentsReadService {

  private static final Logger LOG = Logger.getLogger(EnrollmentsReadServiceImpl.class);

  @Inject EnrollmentsQueries queries;

  /** {@inheritDoc} */
  @Override
  public EnrollmentView getViewByIds(UUID projectId, UUID formerStudentId) {
    return queries
        .findOptionalByIds(projectId, formerStudentId)
        .orElseThrow(
            () -> {
              LOG.debugf(
                  "Enrollment lookup failed: Project %s, FormerStudent %s",
                  projectId, formerStudentId);
              return ExceptionHelper.enrollmentNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<EnrollmentView> listViews() {
    return queries.listAll();
  }

  /** {@inheritDoc} */
  @Override
  public List<EnrollmentView> listViewsByProjectId(UUID projectId) {
    return projectId == null ? List.of() : queries.listAllByProjectId(projectId);
  }

  /** {@inheritDoc} */
  @Override
  public List<EnrollmentView> listViewsByFormerStudentId(UUID formerStudentId) {
    return formerStudentId == null ? List.of() : queries.listAllByFormerStudentId(formerStudentId);
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<EnrollmentView> search(
      EnrollmentComplexSearchCriteria criteria, PageQuery pageQuery) {
    return queries.search(criteria, pageQuery);
  }
}
