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
 * Read-only interface for executing former-student queries.
 */
public interface FormerStudentsQueries {

  Optional<FormerStudentView> findOptionalById(UUID accountId);

  List<FormerStudentView> listAllByIds(List<UUID> accountIds);

  List<FormerStudentView> listAllFormerStudents();

  PageResult<FormerStudentComplexSearchView> search(
      PageQuery pageQuery, FormerStudentComplexSearchCriteria criteria);
}
