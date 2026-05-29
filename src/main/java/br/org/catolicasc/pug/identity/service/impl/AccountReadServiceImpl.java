package br.org.catolicasc.pug.identity.service.impl;

import br.org.catolicasc.pug.identity.infra.read.AccountQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.identity.service.AccountReadService;
import br.org.catolicasc.pug.identity.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link AccountReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * AccountQueries} infrastructure component. It handles basic input validation and translates "not
 * found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class AccountReadServiceImpl implements AccountReadService {

  private static final Logger LOG = Logger.getLogger(AccountReadServiceImpl.class);

  @Inject AccountQueries queries;

  /** {@inheritDoc} */
  @Override
  public AccountView getViewByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      throw ExceptionHelper.accountNotFound();
    }

    return queries
        .findOptionalByEmail(email)
        .orElseThrow(
            () -> {
              LOG.debugf("Account lookup failed: Email %s not found", email);
              return ExceptionHelper.accountNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public AccountView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("Account lookup failed: ID %s not found", id);
              return ExceptionHelper.accountNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<AccountView> listViews() {
    return queries.listAllAccounts();
  }

  /** {@inheritDoc} */
  @Override
  public List<AccountView> listViewsByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    return queries.listByCpf(cpf);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Prior to execution, the input query is "folded" (lowercased and accents removed via {@link
   * StringUtils#fold(String)}) to ensure maximum compatibility with the underlying database filtering
   * rules.
   */
  @Override
  public List<AccountView> search(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}
