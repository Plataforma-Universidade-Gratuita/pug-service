package com.pug.identity.service.utils;

import com.pug.identity.domain.Admin;
import com.pug.shared.domain.enums.Campi;

import java.util.UUID;

/** Utility class for processing Admin DTO inputs. */
public class AdminProcessor {

  /**
   * Helper method to process DTO input and build a new Admin domain object.
   *
   * @param accountId The ID of the account being granted admin rights.
   * @return The constructed Admin domain object.
   */
  public static Admin processCreateInput(UUID accountId) {
    return Admin.factory(accountId);
  }

  /**
   * Helper method to process DTO input and update an existing Admin domain object.
   *
   * @param existingAdmin The existing admin to be updated.
   * @param newCampus The new campus from DTO (can be null for no change).
   * @return The updated Admin domain object.
   */
  public static Admin processUpdateInput(Admin existingAdmin, Campi newCampus) {
    Admin updatedAdmin = existingAdmin;

    if (newCampus != null) {
      updatedAdmin = updatedAdmin.changeCampus(newCampus);
    }

    return updatedAdmin;
  }
}
