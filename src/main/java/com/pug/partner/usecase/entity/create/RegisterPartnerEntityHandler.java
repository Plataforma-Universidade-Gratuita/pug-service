package com.pug.partner.usecase.entity.create;

import com.pug.geo.domain.City;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.exceptions.DuplicateCnpjException;
import com.pug.partner.infra.persistence.PartnerEntityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.UUID;

@ApplicationScoped
public class RegisterPartnerEntityHandler {

  @Inject PartnerEntityRepository repo;
  @Inject EntityManager em;
  @Inject Validator validator;

  @Transactional
  public UUID handle(RegisterPartnerEntityCommand cmd) {
    final String cnpjDigits = cmd.cnpj() == null ? null : cmd.cnpj().replaceAll("\\D+", "");
    final String name = cmd.name() == null ? null : cmd.name().trim();

    City cityStub = cmd.cityId() == null ? null : City.builder().id(cmd.cityId()).build();
    var candidate =
        PartnerEntity.builder()
            .cnpj(cnpjDigits)
            .name(name)
            .city(cityStub)
            .address(cmd.address())
            .build();

    var v = validator.validate(candidate);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);

    if (repo.existsByCnpj(cnpjDigits)) throw new DuplicateCnpjException(cnpjDigits);

    candidate.setCity(em.getReference(City.class, cmd.cityId()));
    repo.persist(candidate);
    repo.flush();
    return candidate.getId();
  }
}
