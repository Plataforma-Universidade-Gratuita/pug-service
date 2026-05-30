package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.service.dtos.EnrollmentComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

public interface EnrollmentsReadService {

  EnrollmentView getViewByIds(UUID projectId, UUID studentId);

  List<EnrollmentView> listViews();

  List<EnrollmentView> listViewsByProjectId(UUID projectId);

  List<EnrollmentView> listViewsByStudentId(UUID studentId);

  PageResult<EnrollmentView> search(EnrollmentComplexSearchCriteria criteria, PageQuery pageQuery);
}
