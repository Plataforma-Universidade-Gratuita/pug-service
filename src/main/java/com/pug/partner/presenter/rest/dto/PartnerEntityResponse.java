package com.pug.partner.presenter.rest.dto;

import com.pug.partner.domain.PartnerEntity;
import java.util.UUID;

public record PartnerEntityResponse(
    UUID id, String cnpj, String name, UUID cityId, String address, boolean active) {

  public static PartnerEntityResponse from(PartnerEntity e) {
    return new PartnerEntityResponse(
        e.getId(),
        e.getCnpj(),
        e.getName(),
        e.getCity() != null ? e.getCity().getId() : null,
        e.getAddress(),
        e.isActive());
  }
}
