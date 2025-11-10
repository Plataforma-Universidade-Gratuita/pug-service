package com.pug.identity.service;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.infra.read.AdminQueries;
import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.shared.exceptions.ResourceNotFoundException;
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
   * @return the admin view.
   * @throws ResourceNotFoundException if the admin is not found.
   */
  public AdminView getView(UUID userId) {
    return queries
        .findOptionalById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND));
  }

  /**
   * Lists all admin views.
   *
   * @return the list of admin views.
   */
  public List<AdminView> listViews() {
    return queries.listAllAdmins();
  }
}
