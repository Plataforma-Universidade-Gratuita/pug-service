package com.pug.partner.service.impl;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.infra.read.IStaffQueries;
import com.pug.partner.infra.read.dtos.StaffView;
import com.pug.partner.service.IStaffReadService;
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
public class StaffReadService implements IStaffReadService {

  @Inject
  IStaffQueries queries;

  @Override
  public StaffView getViewById(UUID id) {
    return queries
            .findOptionalById(id)
            .orElseThrow(
                    () ->
                            new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND, Map.of("id", id)));
  }

  @Override
  public StaffView getViewByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      throw new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND, Map.of("email", email));
    }
    return queries
            .findOptionalByEmail(email)
            .orElseThrow(
                    () ->
                            new ResourceNotFoundException(
                                    PartnerErrorCodes.STAFF_NOT_FOUND, Map.of("email", email)));
  }

  @Override
  public List<StaffView> listViews() {
    return queries.listAllStaff();
  }

  @Override
  public List<StaffView> listViewsByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    return queries.listByCpf(cpf);
  }

  @Override
  public List<StaffView> listViewsByEntityId(UUID entityId) {
    return queries.listAllByEntityId(entityId);
  }

  @Override
  public List<StaffView> search(String term) {
    if (StringUtils.isEmpty(term)) {
      return List.of();
    }
    return queries.searchByName(term);
  }
}