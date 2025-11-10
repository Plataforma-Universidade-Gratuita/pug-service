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
   * @param userId the user ID
   * @return an Optional containing the StaffView if found, otherwise empty
   */
  Optional<StaffView> findOptionalByUserId(UUID userId);

  /**
   * Lists all StaffView records.
   *
   * @return a list of all StaffView records
   */
  List<StaffView> listAllStaff();

  /**
   * Lists all StaffView records by entity ID.
   *
   * @param entityId the entity ID
   * @return a list of StaffView records associated with the given entity ID
   */
  List<StaffView> listAllByEntityId(UUID entityId);
}
