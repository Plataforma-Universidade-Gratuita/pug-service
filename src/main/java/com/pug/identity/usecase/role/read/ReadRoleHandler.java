package com.pug.identity.usecase.role.read;

import com.pug.identity.domain.Role;
import com.pug.identity.domain.exceptions.RoleNotFoundException;
import com.pug.identity.infra.persistence.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class ReadRoleHandler {
  @Inject RoleRepository repo;
  @Inject Validator validator;

  @Transactional(Transactional.TxType.SUPPORTS)
  public Role handle(ReadRoleByIdQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);
    return repo.findByIdOptional(q.id()).orElseThrow(() -> new RoleNotFoundException(q.id()));
  }

  @Transactional(Transactional.TxType.SUPPORTS)
  public Role handle(ReadRoleByEmailQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);

    String email = q.email().trim().toLowerCase(java.util.Locale.ROOT);

    var vEmail = validator.validateValue(Role.class, "email", email);
    if (!vEmail.isEmpty()) throw new ConstraintViolationException(vEmail);

    return repo.findByEmail(email).orElseThrow(() -> new RoleNotFoundException(email));
  }
}
