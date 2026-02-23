package com.pug.partner.service;

import com.pug.partner.infra.read.dtos.StaffView;
import java.util.List;
import java.util.UUID;

/** Interface for reading staff views. */
public interface StaffReadService {

  /**
   * Retrieves a StaffView by its account ID.
   *
   * @param accountId the account ID of the staff member.
   * @return the StaffView associated with the given ID.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no StaffView is found with the
   *     given ID.
   */
  StaffView getViewByAccountId(UUID accountId);

  /**
   * Retrieves a StaffView by email.
   *
   * @param email the email address.
   * @return the StaffView associated with the given email.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no StaffView is found with the
   *     given email.
   */
  StaffView getViewByEmail(String email);

  /**
   * Lists all StaffViews.
   *
   * @return a list of all StaffViews.
   */
  List<StaffView> listViews();

  /**
   * Lists all StaffViews associated with a specific CPF.
   *
   * @param cpf the CPF number.
   * @return a list of StaffViews linked to the specified CPF.
   */
  List<StaffView> listViewsByCpf(String cpf);

  /**
   * Lists all StaffViews associated with a specific entityId ID.
   *
   * @param entityId the entityId ID.
   * @return a list of StaffViews linked to the specified entityId.
   */
  List<StaffView> listViewsByEntityId(UUID entityId);

  /**
   * Searches for StaffViews by name.
   *
   * @param term the search term (typically a account's name).
   * @return a list of StaffViews matching the search term.
   */
  List<StaffView> search(String term);
}
