package com.pug.partner.presenter.rest.dto;

import com.pug.partner.domain.Staff;
import java.util.UUID;

public record StaffResponse(UUID id, UUID userRoleId, UUID entityId) {
  public static StaffResponse from(Staff s) {
    return new StaffResponse(
        s.getId(),
        s.getUserRole() != null ? s.getUserRole().getId() : null,
        s.getEntity() != null ? s.getEntity().getId() : null);
  }
}
