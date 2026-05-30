package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentComplexSearchView;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/**
 * Application-layer read contract dedicated to former-student views and search results.
 *
 * <p>This service exposes the academic read use cases consumed by the presenter layer while hiding
 * the underlying query implementation details. It returns immutable read projections instead of
 * domain aggregates because these flows are display-oriented.
 */
public interface FormerStudentsReadService {

  FormerStudentView getViewByAccountId(UUID accountId);

  List<FormerStudentView> listViews();

  List<FormerStudentView> listViewsByIds(List<UUID> accountIds);

  PageResult<FormerStudentComplexSearchView> search(
      PageQuery pageQuery, FormerStudentComplexSearchCriteria criteria);
}
