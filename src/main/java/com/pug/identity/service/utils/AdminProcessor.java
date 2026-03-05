package com.pug.identity.service.utils;

import com.pug.identity.domain.Admin;
import com.pug.shared.domain.enums.Campi;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link Admin}
 * Domain Aggregates.
 */
public class AdminProcessor {

  /**
   * Processes raw creation inputs and constructs a new {@link Admin} domain aggregate.
   *
   * <p><b>Note:</b> The caller is responsible for checking {@link Admin#hasFieldErrors()} on the
   * returned object and throwing standard validation exceptions if necessary.
   *
   * @param accountId the unique identifier of the linked authentication account
   * @param campus the designated university campus for the administrator
   * @return a fully instantiated {@link Admin} domain aggregate, potentially containing validation
   *     errors
   */
  public static Admin processCreateInput(UUID accountId, Campi campus) {
    return Admin.factory(accountId, campus);
  }

  /**
   * Processes raw update inputs and conditionally mutates the state of an existing {@link Admin}.
   *
   * <p>Applies partial updates, returning a new immutable instance of the aggregate.
   *
   * @param existingAdmin the current, reconstituted {@link Admin} aggregate from the repository
   * @param newCampus the proposed new campus, or {@code null} to skip updating
   * @return a new {@link Admin} domain aggregate reflecting the requested updates, potentially
   *     containing validation errors
   */
  public static Admin processUpdateInput(Admin existingAdmin, Campi newCampus) {
    Admin updatedAdmin = existingAdmin;

    if (newCampus != null) {
      updatedAdmin = updatedAdmin.changeCampus(newCampus);
    }

    return updatedAdmin;
  }
}
