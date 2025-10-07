package com.pug.partner.usecase.staff.read;

import com.pug.partner.domain.Staff;
import com.pug.partner.domain.exceptions.StaffNotFoundException;
import com.pug.partner.infra.persistence.StaffRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class ReadStaffHandler {

  @Inject StaffRepository repo;
  @Inject Validator validator;

  @Transactional(Transactional.TxType.SUPPORTS)
  public Staff handle(ReadStaffByUserRoleIdQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);
    return repo.findByUserRoleId(q.userRoleId())
        .orElseThrow(() -> new StaffNotFoundException(q.userRoleId()));
  }
}
