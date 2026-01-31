package com.pug.identity.service;

import com.pug.identity.infra.read.dtos.UserView;
import java.util.List;
import java.util.UUID;

/** Interface for reading user views. */
public interface IUserReadService {

  /**
   * Retrieves a UserView by its unique identifier.
   *
   * @param id the UUID of the user
   * @return the UserView
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no user with the given ID is
   *     found
   */
  UserView getViewById(UUID id);

  /**
   * Retrieves a UserView by its CPF.
   *
   * @param cpf the CPF of the user
   * @return the UserView
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no user with the given CPF is
   *     found
   */
  UserView getViewByCpf(String cpf);

  /**
   * Lists all UserViews.
   *
   * @return a list of all UserViews
   */
  List<UserView> listViews();

  /**
   * Searches for UserViews by name.
   *
   * @param query the search query
   * @return a list of UserViews matching the search query
   */
  List<UserView> search(String query);
}
