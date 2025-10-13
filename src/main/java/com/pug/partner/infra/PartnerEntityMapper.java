package com.pug.partner.infra;

import com.pug.partner.domain.Address;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.infra.persistence.PartnerEntityEntity;

public final class PartnerEntityMapper {
  private PartnerEntityMapper() {}

  public static PartnerEntity toDomain(PartnerEntityEntity e) {
    if (e == null) return null;
    return PartnerEntity.builder()
        .id(e.getId())
        .cnpj(e.getCnpj())
        .name(e.getName())
        .cityId(e.getCityId())
        .address(Address.of(e.getAddress()))
        .active(e.isActive())
        .build();
  }

  public static PartnerEntityEntity toEntity(PartnerEntity d) {
    if (d == null) return null;
    return PartnerEntityEntity.builder()
        .id(d.getId())
        .cnpj(d.getCnpj())
        .name(d.getName())
        .cityId(d.getCityId())
        .address(d.getAddress() == null ? null : d.getAddress().toString())
        .active(d.isActive())
        .build();
  }

  /** Copy domain fields into a managed entity (no id touch). */
  public static void copy(PartnerEntity d, PartnerEntityEntity e) {
    e.setCnpj(d.getCnpj());
    e.setName(d.getName());
    e.setCityId(d.getCityId());
    e.setAddress(d.getAddress() == null ? null : d.getAddress().toString());
    e.setActive(d.isActive());
  }
}
