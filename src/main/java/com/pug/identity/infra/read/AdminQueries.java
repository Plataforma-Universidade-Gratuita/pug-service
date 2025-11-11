package com.pug.identity.infra.read;

import com.pug.identity.infra.read.dtos.AdminView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Queries related to Admins. */
public interface AdminQueries {
  /**
   * Find AdminView by accountId.
   *
   * @param accountId the account ID of the admin.
   * @return an Optional containing the AdminView if found, otherwise empty.
   */
  Optional<AdminView> findOptionalById(UUID accountId);

  /**
   * List all AdminViews.
   *
   * @return a list of all AdminViews.
   */
  List<AdminView> listAllAdmins();
}
