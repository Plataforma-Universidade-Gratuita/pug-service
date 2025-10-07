package com.pug.partner.usecase.entity.update;

import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.exceptions.DuplicateCnpjException;
import com.pug.partner.domain.exceptions.PartnerEntityNotFoundException;
import com.pug.partner.infra.persistence.PartnerEntityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class UpdatePartnerEntityHandler {

  @Inject PartnerEntityRepository repo;
  @Inject EntityManager em;
  @Inject Validator validator;

  @Transactional
  public PartnerEntity handle(UpdatePartnerEntityCommand cmd) {
    String cnpjDigits = cmd.cnpj() == null ? null : cmd.cnpj().replaceAll("\\D+", "");
    String name = cmd.name() == null ? null : cmd.name().trim();

    var vCmd =
        validator.validate(
            new UpdatePartnerEntityCommand(
                cmd.id(), cnpjDigits, name, cmd.cityId(), cmd.address()));
    if (!vCmd.isEmpty()) throw new ConstraintViolationException(vCmd);

    var entity =
        repo.findByIdOptional(cmd.id())
            .orElseThrow(() -> new PartnerEntityNotFoundException(cmd.id()));

    if (cnpjDigits != null
        && !cnpjDigits.equals(entity.getCnpj())
        && repo.existsByCnpjForAnother(cnpjDigits, entity.getId())) {
      throw new DuplicateCnpjException(cnpjDigits);
    }

    var probe =
        com.pug.partner.domain.PartnerEntity.builder()
            .id(entity.getId())
            .cnpj(cnpjDigits != null ? cnpjDigits : entity.getCnpj())
            .name(name != null ? name : entity.getName())
            .city(
                cmd.cityId() != null
                    ? com.pug.geo.domain.City.builder().id(cmd.cityId()).build()
                    : entity.getCity())
            .address(cmd.address() != null ? cmd.address() : entity.getAddress())
            .active(entity.isActive())
            .build();

    var vEntity = validator.validate(probe);
    if (!vEntity.isEmpty()) throw new ConstraintViolationException(vEntity);

    if (cnpjDigits != null) entity.setCnpj(cnpjDigits);
    if (name != null) entity.setName(name);
    if (cmd.address() != null) entity.setAddress(cmd.address());
    if (cmd.cityId() != null)
      entity.setCity(em.getReference(com.pug.geo.domain.City.class, cmd.cityId()));

    repo.flush();
    return entity;
  }

  @Transactional
  public void handle(UpdateIsActiveCommand cmd) {
    var v = validator.validate(cmd);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);

    var entity =
        repo.findByIdOptional(cmd.id())
            .orElseThrow(() -> new PartnerEntityNotFoundException(cmd.id()));

    entity.setActive(!entity.isActive());
    repo.flush();
  }
}
