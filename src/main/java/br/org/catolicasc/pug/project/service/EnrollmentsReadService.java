package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/**
 * Application-layer read contract for enrollment projections and search results.
 *
 * <p>This service exposes the read use cases consumed by enrollment presenters, including
 * composite-key lookups, collection reads scoped by project or former student, and paginated
 * complex search.
 */
public interface EnrollmentsReadService {

  EnrollmentView getViewByIds(UUID projectId, UUID formerStudentId);

  List<EnrollmentView> listViews();

  List<EnrollmentView> listViewsByProjectId(UUID projectId);

  List<EnrollmentView> listViewsByFormerStudentId(UUID formerStudentId);

  PageResult<EnrollmentView> search(EnrollmentComplexSearchCriteria criteria, PageQuery pageQuery);
}
