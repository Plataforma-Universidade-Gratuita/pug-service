package br.org.catolicasc.pug.partner.service.impl;

import br.org.catolicasc.pug.partner.infra.read.StaffQueries;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.service.StaffReadService;
import br.org.catolicasc.pug.partner.service.dtos.StaffComplexSearchCriteria;
import br.org.catolicasc.pug.partner.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Implementation of the {@link StaffReadService}. */
@ApplicationScoped
public class StaffReadServiceImpl implements StaffReadService {

  private static final Logger LOG = Logger.getLogger(StaffReadServiceImpl.class);

  @Inject StaffQueries queries;

  @Override
  public StaffView getViewByAccountId(UUID accountId) {
    return queries
        .findOptionalById(accountId)
        .orElseThrow(
            () -> {
              LOG.debugf("Staff lookup failed: Account ID %s not found", accountId);
              return ExceptionHelper.staffNotFound();
            });
  }

  @Override
  public List<StaffView> listViews() {
    return queries.listAllStaff();
  }

  @Override
  public List<StaffView> listViewsByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return queries.listAllByIds(ids);
  }

  @Override
  public PageResult<StaffComplexSearchView> search(
      PageQuery pageQuery, StaffComplexSearchCriteria criteria) {
    return queries.search(pageQuery, criteria);
  }
}
