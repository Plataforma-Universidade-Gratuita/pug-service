package com.pug.partner.infra.read;

import com.pug.partner.infra.read.dtos.StaffView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Queries related to Staff. */
public interface StaffQueries {
  /**
   * Finds a StaffView by user ID.
   *
   * @param userId the UUID of the user.
   * @return an Optional containing the StaffView if found, or empty if not found.
   */
  Optional<StaffView> findByUserId(UUID userId);

  /**
   * Lists all StaffView entries.
   *
   * @return a list of all StaffView entries.
   */
  List<StaffView> listAll();

  /**
   * Lists all StaffView entries by entity ID.
   *
   * @param entityId the UUID of the entity.
   * @return a list of StaffView entries associated with the specified entity ID.
   */
  List<StaffView> listAllByEntityId(UUID entityId);
}
