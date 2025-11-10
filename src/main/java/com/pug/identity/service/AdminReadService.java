package com.pug.identity.service;

import com.pug.identity.infra.queries.AdminQueries;
import com.pug.identity.presenter.dtos.AdminView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

/** Service for reading admin data. */
@ApplicationScoped
public class AdminReadService {

  @Inject AdminQueries queries;

  /**
   * Gets the admin view by user ID.
   *
   * @param userId the user ID.
   * @return the admin view, or null if not found.
   */
  public AdminView getView(UUID userId) {
    return queries.findById(userId).orElse(null);
  }

  /**
   * Lists all admin views.
   *
   * @return the list of admin views.
   */
  public List<AdminView> listViews() {
    return queries.listAll();
  }
}
