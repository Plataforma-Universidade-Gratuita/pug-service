package com.pug.identity.usecase.role.get.byEmail;

import com.pug.identity.domain.Role;
import com.pug.identity.domain.exceptions.RoleNotFoundException;
import com.pug.identity.infra.persistence.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class GetRoleByEmailHandler {
  @Inject RoleRepository repo;
  @Inject Validator validator;

  @Transactional(Transactional.TxType.SUPPORTS)
  public Role handle(GetRoleByEmailQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);

    String email = q.email().trim().toLowerCase(java.util.Locale.ROOT);

    var vEmail = validator.validateValue(Role.class, "email", email);
    if (!vEmail.isEmpty()) throw new ConstraintViolationException(vEmail);

    return repo.findByEmail(email).orElseThrow(() -> new RoleNotFoundException(email));
  }
}
