package br.org.catolicasc.pug.academic.service.impl;

import br.org.catolicasc.pug.academic.infra.read.AreasOfExpertiseQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.service.AreasOfExpertiseReadService;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseComplexSearchCriteria;
import br.org.catolicasc.pug.academic.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Query-side service implementation for academic areas of expertise. */
@jakarta.enterprise.context.ApplicationScoped
public class AreasOfExpertiseReadServiceImpl implements AreasOfExpertiseReadService {

  private static final Logger LOG = Logger.getLogger(AreasOfExpertiseReadServiceImpl.class);

  @jakarta.inject.Inject AreasOfExpertiseQueries queries;

  @Override
  public SchoolView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("Area of expertise lookup failed: ID %s not found", id);
              return ExceptionHelper.schoolNotFound();
            });
  }

  @Override
  public List<SchoolView> listViews() {
    return queries.listAllViews();
  }

  @Override
  public List<SchoolView> listViewsByIds(List<UUID> ids) {
    return queries.listAllByIds(ids);
  }

  @Override
  public PageResult<SchoolView> search(
      PageQuery pageQuery, AreaOfExpertiseComplexSearchCriteria criteria) {
    return queries.search(pageQuery, criteria);
  }
}
