package com.pug.partner.service;

import com.pug.partner.infra.read.StaffQueries;
import com.pug.partner.infra.read.dtos.StaffView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

/** Read-only service for staff views. */
@ApplicationScoped
public class StaffReadService {

  @Inject StaffQueries queries;

  /**
   * Retrieves a StaffView by user ID.
   *
   * @param userId the UUID of the user.
   * @return the StaffView associated with the user ID, or null if not found.
   */
  public StaffView getView(UUID userId) {
    return queries.findByUserId(userId).orElse(null);
  }

  /**
   * Lists all StaffViews.
   *
   * @return a list of all StaffViews.
   */
  public List<StaffView> listViews() {
    return queries.listAll();
  }

  /**
   * Lists all StaffViews by entity ID.
   *
   * @param entityId the UUID of the entity.
   * @return a list of StaffViews associated with the entity ID.
   */
  public List<StaffView> listViewsByEntityId(UUID entityId) {
    return queries.listAllByEntityId(entityId);
  }
}
