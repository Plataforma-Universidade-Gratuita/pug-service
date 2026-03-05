package com.pug.academic.service.impl;

import com.pug.academic.infra.read.SchoolQueries;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.academic.service.SchoolReadService;
import com.pug.academic.service.utils.ExceptionHelper;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link SchoolReadService}.
 * <p>
 * This application-scoped bean delegates read-only operations to the underlying
 * {@link SchoolQueries} infrastructure component. It handles basic input validation
 * and translates "not found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class SchoolReadServiceImpl implements SchoolReadService {

  private static final Logger LOG = Logger.getLogger(SchoolReadServiceImpl.class);

  @Inject
  SchoolQueries queries;

  /**
   * {@inheritDoc}
   */
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

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SchoolView> listAll() {
    return queries.listAllSchools();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Prior to execution, the input query is "folded" (lowercased and accents removed via
   * {@link StringUtils#fold(String)}) to ensure maximum compatibility with the
   * underlying search indexing rules.
   */
  @Override
  public List<SchoolView> searchByName(String key) {
    return queries.searchByName(StringUtils.fold(key));
  }
}