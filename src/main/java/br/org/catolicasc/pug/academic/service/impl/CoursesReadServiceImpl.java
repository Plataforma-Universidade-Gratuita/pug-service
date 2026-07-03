/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.service.impl;

import br.org.catolicasc.pug.academic.infra.read.CoursesQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import br.org.catolicasc.pug.academic.service.CoursesReadService;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseComplexSearchCriteria;
import br.org.catolicasc.pug.academic.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link CoursesReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * CoursesQueries} infrastructure component. It also translates not-found states into standardized
 * domain exceptions.
 */
@jakarta.enterprise.context.ApplicationScoped
public class CoursesReadServiceImpl implements CoursesReadService {

  private static final Logger LOG = Logger.getLogger(CoursesReadServiceImpl.class);

  @jakarta.inject.Inject CoursesQueries queries;

  /** {@inheritDoc} */
  @Override
  public CourseView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("Course lookup failed: ID %s not found", id);
              return ExceptionHelper.courseNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<CourseView> listViews() {
    return queries.listAllCourses();
  }

  /** {@inheritDoc} */
  @Override
  public List<CourseView> listViewsByIds(List<UUID> ids) {
    return queries.listAllByIds(ids);
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<CourseView> search(PageQuery pageQuery, CourseComplexSearchCriteria criteria) {
    return queries.search(pageQuery, criteria);
  }
}
