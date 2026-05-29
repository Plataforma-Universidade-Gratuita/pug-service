package br.org.catolicasc.pug.academic.service.impl;

import br.org.catolicasc.pug.academic.infra.read.SchoolQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.academic.service.SchoolReadService;
import br.org.catolicasc.pug.academic.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link SchoolReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * SchoolQueries} infrastructure component. It handles basic input validation and translates "not
 * found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class SchoolReadServiceImpl implements SchoolReadService {

  private static final Logger LOG = Logger.getLogger(SchoolReadServiceImpl.class);

  @Inject SchoolQueries queries;

  /** {@inheritDoc} */
  @Override
  public SchoolView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("School lookup failed: ID %s not found", id);
              return ExceptionHelper.schoolNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<SchoolView> listAll() {
    return queries.listAllSchools();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Prior to execution, the input query is "folded" (lowercased and accents removed via {@link
   * StringUtils#fold(String)}) to ensure maximum compatibility with the underlying database
   * filtering rules.
   */
  @Override
  public List<SchoolView> searchByName(String key) {
    return queries.searchByName(StringUtils.fold(key));
  }
}
