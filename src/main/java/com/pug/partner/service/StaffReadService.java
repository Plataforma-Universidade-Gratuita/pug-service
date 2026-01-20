package com.pug.partner.service;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.infra.read.StaffQueries;
import com.pug.partner.infra.read.dtos.StaffView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Read-only service for staff views.
 */
@ApplicationScoped
public class StaffReadService {

  @Inject
  StaffQueries queries;

  /**
   * Retrieves a StaffView by its account ID.
   *
   * @param id the account ID of the staff member.
   * @return the StaffView associated with the given ID.
   * @throws ResourceNotFoundException if no StaffView is found with the given ID.
   */
  public StaffView getViewById(UUID id) {
    return queries
            .findOptionalById(id)
            .orElseThrow(
                    () ->
                            new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND, Map.of("id", id)));
  }

  /**
   * Retrieves a StaffView by email.
   *
   * @param email the email address.
   * @return the StaffView associated with the given email.
   * @throws ResourceNotFoundException if no StaffView is found with the given email.
   */
  public StaffView getViewByEmail(String email) {
    return queries
            .findOptionalByEmail(email)
            .orElseThrow(
                    () ->
                            new ResourceNotFoundException(
                                    PartnerErrorCodes.STAFF_NOT_FOUND, Map.of("email", email)));
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
   * Lists all StaffViews associated with a specific CPF.
   *
   * @param cpf the CPF number.
   * @return a list of StaffViews linked to the specified CPF.
   */
  public List<StaffView> listViewsByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    return queries.listByCpf(cpf);
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

  /**
   * Searches for StaffViews by name.
   *
   * @param term the search term (typically a user's name).
   * @return a list of StaffViews matching the search term.
   */
  public List<StaffView> search(String term) {
    if (StringUtils.isEmpty(term)) {
      return List.of();
    }
    return queries.searchByName(term);
  }
}