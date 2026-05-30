package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.service.dtos.AreaOfExpertiseComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/** Query-side service for academic areas of expertise. */
public interface AreasOfExpertiseReadService {
  SchoolView getViewById(UUID id);

  List<SchoolView> listViews();

  List<SchoolView> listViewsByIds(List<UUID> ids);

  PageResult<SchoolView> search(PageQuery pageQuery, AreaOfExpertiseComplexSearchCriteria criteria);
}
