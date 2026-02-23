package com.pug.partner.service.impl;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.infra.read.StaffQueries;
import com.pug.partner.infra.read.dtos.StaffView;
import com.pug.partner.service.StaffReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Read-only service for staff views. */
@ApplicationScoped
public class StaffReadServiceImpl implements StaffReadService {

  private static final Logger LOG = Logger.getLogger(StaffReadServiceImpl.class);

  @Inject StaffQueries queries;

  @Override
  public StaffView getViewByAccountId(UUID accountId) {
    return queries
        .findOptionalById(accountId)
        .orElseThrow(
            () -> {
              LOG.debugf("Staff lookup failed: Account ID %s not found", accountId);
              return new ResourceNotFoundException(
                  PartnerErrorCodes.STAFF_NOT_FOUND, "accountId", accountId.toString());
            });
  }

  @Override
  public StaffView getViewByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      throw new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND, "email", "empty");
    }

    return queries
        .findOptionalByEmail(email)
        .orElseThrow(
            () -> {
              LOG.debugf("Staff lookup failed: Email %s not found", email);
              return new ResourceNotFoundException(
                  PartnerErrorCodes.STAFF_NOT_FOUND, "email", email);
            });
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
    if (entityId == null) {
      return List.of();
    }
    return queries.listAllByEntityId(entityId);
  }

  @Override
  public List<StaffView> search(String term) {
    if (StringUtils.isEmpty(term)) {
      return List.of();
    }
    return queries.searchByName(StringUtils.fold(term));
  }
}
