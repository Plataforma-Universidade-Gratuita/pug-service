package com.pug.identity.service;

import com.pug.identity.infra.read.dtos.AdminView;
import java.util.List;
import java.util.UUID;

/** Interface for reading admin data. */
public interface AdminReadService {

  /**
   * Retrieves an AdminView by its account ID.
   *
   * @param accountId the account ID of the admin.
   * @return the AdminView.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no admin with the given account
   *     ID is found.
   */
  AdminView getViewById(UUID accountId);

  /**
   * Retrieves an AdminView by its email.
   *
   * @param email the email of the admin.
   * @return the AdminView.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no admin with the given email is
   *     found.
   */
  AdminView getViewByEmail(String email);

  /**
   * Lists all AdminViews.
   *
   * @return a list of all AdminViews.
   */
  List<AdminView> listViews();

  /**
   * Lists AdminViews by CPF.
   *
   * @param cpf the CPF to filter by.
   * @return a list of AdminViews matching the given CPF.
   */
  List<AdminView> listViewsByCpf(String cpf);

  /**
   * Searches for AdminViews by name (of the associated user).
   *
   * @param query the search query.
   * @return a list of matching AdminViews.
   */
  List<AdminView> search(String query);
}
