package br.org.catolicasc.pug.project.infra.read;

import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentsQueries {

  Optional<EnrollmentView> findOptionalByIds(UUID projectId, UUID formerStudentId);

  List<EnrollmentView> listAll();

  List<EnrollmentView> listAllByProjectId(UUID projectId);

  List<EnrollmentView> listAllByFormerStudentId(UUID formerStudentId);

  PageResult<EnrollmentView> search(EnrollmentComplexSearchCriteria criteria, PageQuery pageQuery);
}
