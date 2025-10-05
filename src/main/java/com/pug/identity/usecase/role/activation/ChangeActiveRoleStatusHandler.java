package com.pug.identity.usecase.role.activation;

import com.pug.identity.domain.exceptions.RoleNotFoundException;
import com.pug.identity.infra.persistence.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class ChangeActiveRoleStatusHandler {
  @Inject RoleRepository repo;
  @Inject Validator validator;

  @Transactional
  public void handle(ChangeActiveRoleStatusCommand cmd) {
    var v = validator.validate(cmd);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);

    var entity =
        repo.findByIdOptional(cmd.id()).orElseThrow(() -> new RoleNotFoundException(cmd.id()));

    entity.setActive(!entity.isActive());
    repo.flush();
  }
}
