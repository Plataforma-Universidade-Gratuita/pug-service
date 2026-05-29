package br.org.catolicasc.pug.partner.service.impl;

import br.org.catolicasc.pug.partner.infra.read.StaffQueries;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.service.StaffReadService;
import br.org.catolicasc.pug.partner.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link StaffReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * StaffQueries} infrastructure component. It handles basic input validation and translates "not
 * found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class StaffReadServiceImpl implements StaffReadService {

  private static final Logger LOG = Logger.getLogger(StaffReadServiceImpl.class);

  @Inject StaffQueries queries;

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public StaffView getViewByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      throw ExceptionHelper.staffNotFound();
    }

    return queries
        .findOptionalByEmail(email)
        .orElseThrow(
            () -> {
              LOG.debugf("Staff lookup failed: Email %s not found", email);
              return ExceptionHelper.staffNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<StaffView> listViews() {
    return queries.listAllStaff();
  }

  /** {@inheritDoc} */
  @Override
  public List<StaffView> listViewsByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    return queries.listByCpf(cpf);
  }

  /** {@inheritDoc} */
  @Override
  public List<StaffView> listViewsByEntityId(UUID entityId) {
    if (entityId == null) {
      return List.of();
    }
    return queries.listAllByEntityId(entityId);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Prior to execution, the input query is "folded" (lowercased and accents removed via {@link
   * StringUtils#fold(String)}) to ensure maximum compatibility with the underlying database
   * filtering rules.
   */
  @Override
  public List<StaffView> search(String term) {
    if (StringUtils.isEmpty(term)) {
      return List.of();
    }
    return queries.searchByName(StringUtils.fold(term));
  }
}
