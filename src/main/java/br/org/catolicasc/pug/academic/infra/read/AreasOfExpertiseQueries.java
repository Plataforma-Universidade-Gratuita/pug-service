package br.org.catolicasc.pug.academic.infra.read;

import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only query contract for academic areas-of-expertise projections.
 *
 * <p>This boundary centralizes the lookup, listing, and paginated complex-search operations used by
 * the academic read side. Implementations are expected to return lightweight projections rather
 * than managed entities so presenter flows can remain decoupled from persistence concerns.
 */
public interface AreasOfExpertiseQueries {
  Optional<AreaOfExpertiseView> findOptionalById(UUID id);

  List<AreaOfExpertiseView> listAllByIds(List<UUID> ids);

  List<AreaOfExpertiseView> listAllViews();

  PageResult<AreaOfExpertiseView> search(
      PageQuery pageQuery, AreaOfExpertiseComplexSearchCriteria criteria);
}
