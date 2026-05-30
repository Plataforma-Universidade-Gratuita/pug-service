package br.org.catolicasc.pug.academic.infra.read;

import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentComplexSearchView;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only query contract for former-student projections.
 *
 * <p>This boundary exposes the infrastructure queries that power direct lookups, collection reads,
 * and paginated complex-search flows for former students. It returns read-model projections shaped
 * for presenter and service consumption without leaking persistence entities upward.
 */
public interface FormerStudentsQueries {

  Optional<FormerStudentView> findOptionalById(UUID accountId);

  List<FormerStudentView> listAllByIds(List<UUID> accountIds);

  List<FormerStudentView> listAllFormerStudents();

  PageResult<FormerStudentComplexSearchView> search(
      PageQuery pageQuery, FormerStudentComplexSearchCriteria criteria);
}
