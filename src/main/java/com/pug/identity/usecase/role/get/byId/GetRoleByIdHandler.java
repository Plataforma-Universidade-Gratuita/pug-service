package com.pug.identity.usecase.role.get.byId;

import com.pug.identity.domain.Role;
import com.pug.identity.domain.exceptions.RoleNotFoundException;
import com.pug.identity.infra.persistence.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class GetRoleByIdHandler {
  @Inject RoleRepository repo;
  @Inject Validator validator;

  @Transactional(Transactional.TxType.SUPPORTS)
  public Role handle(GetRoleByIdQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);
    return repo.findByIdOptional(q.id()).orElseThrow(() -> new RoleNotFoundException(q.id()));
  }
}
