package com.pug.partner.service.utils;

import com.pug.partner.domain.Staff;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link Staff}
 * Domain Aggregates.
 */
public class StaffProcessor {

  /**
   * Processes raw creation inputs and constructs a new {@link Staff} domain aggregate.
   *
   * <p><b>Note:</b> The caller is responsible for checking {@link Staff#hasFieldErrors()} on the
   * returned object and throwing standard validation exceptions if necessary.
   *
   * @param accountId the unique identifier of the linked authentication account
   * @param entityId the unique identifier of the linked partner organization
   * @return a fully instantiated {@link Staff} domain aggregate, potentially containing validation
   *     errors
   */
  public static Staff processCreateInput(UUID accountId, UUID entityId) {
    return Staff.factory(accountId, entityId);
  }
}
