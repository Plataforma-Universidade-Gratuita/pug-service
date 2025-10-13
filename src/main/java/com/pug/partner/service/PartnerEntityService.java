package com.pug.partner.service;

import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_CNPJ_ALREADY_EXISTS;
import static com.pug.partner.domain.PartnerErrorCodes.PARTNER_NOT_FOUND;

import com.pug.partner.domain.Address;
import com.pug.partner.domain.Cnpj;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.PartnerEntityRepository;
import com.pug.partner.service.commands.CreatePartnerEntityCommand;
import com.pug.partner.service.commands.UpdatePartnerEntityCommand;
import com.pug.partner.service.queries.ListEntitiesByCityQuery;
import com.pug.shared.application.UuidQuery;
import com.pug.shared.domain.exceptions.AppValidationException;
import com.pug.shared.infra.persistence.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class PartnerEntityService {

  @Inject PartnerEntityRepository repo;

  @Transactional
  public PartnerEntity register(CreatePartnerEntityCommand cmd) {
    if (repo.existsByCnpjForAnother(cmd.cnpj(), null)) {
      throw new AppValidationException(PARTNER_CNPJ_ALREADY_EXISTS);
    }
    PartnerEntity pe =
        PartnerEntity.newActive()
            .id(UUID.randomUUID())
            .cnpj(Cnpj.of(cmd.cnpj()))
            .name(cmd.name())
            .cityId(cmd.cityId())
            .address(Address.of(cmd.address()))
            .build();
    return repo.save(pe);
  }

  @Transactional
  public PartnerEntity update(UpdatePartnerEntityCommand cmd) {
    PartnerEntity existing =
        repo.findOptionalById(cmd.id())
            .orElseThrow(() -> new AppValidationException(PARTNER_NOT_FOUND));

    if (repo.existsByCnpjForAnother(cmd.cnpj(), cmd.id())) {
      throw new AppValidationException(PARTNER_CNPJ_ALREADY_EXISTS);
    }

    boolean newActive = cmd.active() == null ? existing.isActive() : cmd.active();

    PartnerEntity updated =
        PartnerEntity.builder()
            .id(existing.getId())
            .cnpj(Cnpj.of(cmd.cnpj()))
            .name(cmd.name())
            .cityId(cmd.cityId())
            .address(Address.of(cmd.address()))
            .active(newActive)
            .build();

    return repo.save(updated);
  }

  public PartnerEntity get(UuidQuery q) {
    return repo.findOptionalById(q.id())
        .orElseThrow(() -> new AppValidationException(PARTNER_NOT_FOUND));
  }

  public Page<PartnerEntity> listByCity(ListEntitiesByCityQuery q) {
    return repo.listByCity(q.cityId(), q.pageRequest());
  }
}
