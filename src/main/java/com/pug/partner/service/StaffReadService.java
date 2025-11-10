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
   * @param userId the user ID of the staff member.
   * @return the StaffView if found, otherwise null.
   */
  public StaffView getView(UUID userId) {
    return queries.findOptionalByUserId(userId).orElse(null);
  }

  /**
   * Lists all StaffViews.
   *
   * @return a list of all StaffViews.
   */
  public List<StaffView> listViews() {
    return queries.listAllStaff();
  }

  /**
   * Lists all StaffViews associated with a specific entity ID.
   *
   * @param entityId the entity ID.
   * @return a list of StaffViews linked to the specified entity.
   */
  public List<StaffView> listViewsByEntityId(UUID entityId) {
    return queries.listAllByEntityId(entityId);
  }
}
