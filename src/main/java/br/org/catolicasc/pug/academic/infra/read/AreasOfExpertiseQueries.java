package br.org.catolicasc.pug.academic.infra.read;

import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Read-only query contract for academic areas of expertise. */
public interface AreasOfExpertiseQueries {
  Optional<SchoolView> findOptionalById(UUID id);

  List<SchoolView> listAllByIds(List<UUID> ids);

  List<SchoolView> listAllViews();

  PageResult<SchoolView> search(PageQuery pageQuery, AreaOfExpertiseComplexSearchCriteria criteria);
}
