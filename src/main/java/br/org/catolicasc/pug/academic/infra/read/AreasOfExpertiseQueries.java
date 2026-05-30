package br.org.catolicasc.pug.academic.infra.read;

import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Read-only query contract for academic areas of expertise. */
public interface AreasOfExpertiseQueries {
  Optional<AreaOfExpertiseView> findOptionalById(UUID id);

  List<AreaOfExpertiseView> listAllByIds(List<UUID> ids);

  List<AreaOfExpertiseView> listAllViews();

  PageResult<AreaOfExpertiseView> search(
      PageQuery pageQuery, AreaOfExpertiseComplexSearchCriteria criteria);
}
