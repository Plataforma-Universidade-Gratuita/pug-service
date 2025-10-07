// src/main/java/com/pug/partner/usecase/entity/get/RetrievePartnerEntityHandler.java
package com.pug.partner.usecase.entity.read;

import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.exceptions.PartnerEntityNotFoundException;
import com.pug.partner.infra.persistence.PartnerEntityRepository;
import com.pug.shared.dtos.ReadByIdQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class ReadPartnerEntityHandler {

  @Inject PartnerEntityRepository repo;
  @Inject Validator validator;

  @Transactional(Transactional.TxType.SUPPORTS)
  public PartnerEntity handle(ReadByIdQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);
    return repo.findByIdOptional(q.id())
        .orElseThrow(() -> new PartnerEntityNotFoundException(q.id()));
  }

  @Transactional(Transactional.TxType.SUPPORTS)
  public PartnerEntity handle(ReadPartnerEntityByCnpjQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);

    String digits = q.cnpj().replaceAll("\\D+", "");
    var vCnpj = validator.validateValue(PartnerEntity.class, "cnpj", digits);
    if (!vCnpj.isEmpty()) throw new ConstraintViolationException(vCnpj);

    return repo.findByCnpj(digits).orElseThrow(() -> new PartnerEntityNotFoundException(digits));
  }
}
