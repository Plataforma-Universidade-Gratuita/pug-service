package br.org.catolicasc.pug.identity.service.impl;

import br.org.catolicasc.pug.identity.infra.read.UserQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
import br.org.catolicasc.pug.identity.service.UserReadService;
import br.org.catolicasc.pug.identity.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link UserReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * UserQueries} infrastructure component. It handles basic input validation and translates "not
 * found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class UserReadServiceImpl implements UserReadService {

  private static final Logger LOG = Logger.getLogger(UserReadServiceImpl.class);

  @Inject UserQueries queries;

  /** {@inheritDoc} */
  @Override
  public UserView getViewByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      throw ExceptionHelper.userNotFound();
    }

    return queries
        .findOptionalByCpf(cpf)
        .orElseThrow(
            () -> {
              LOG.debugf("User lookup failed: CPF %s not found", cpf);
              return ExceptionHelper.userNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public UserView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("User lookup failed: ID %s not found", id);
              return ExceptionHelper.userNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<UserView> listViews() {
    return queries.listAllUsers();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Prior to execution, the input query is "folded" (lowercased and accents removed via {@link
   * StringUtils#fold(String)}) to ensure maximum compatibility with the underlying database
   * filtering rules.
   */
  @Override
  public List<UserView> search(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}
