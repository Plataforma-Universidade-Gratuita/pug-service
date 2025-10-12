// CreateRoleHandler.java
package com.pug.identity.usecase.role.create;

import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import com.pug.identity.domain.exceptions.DuplicateEmailException;
import com.pug.identity.domain.exceptions.FormerStudentRegistrationException;
import com.pug.identity.infra.persistence.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Locale;
import java.util.UUID;

@ApplicationScoped
public class CreateRoleHandler {
  @Inject RoleRepository repo;
  @Inject Validator validator;

  @Transactional
  public UUID handle(CreateRoleCommand cmd) {
    var vCmd = validator.validate(cmd);
    if (!vCmd.isEmpty()) throw new ConstraintViolationException(vCmd);

    String email = cmd.email().trim().toLowerCase(Locale.ROOT);

    var ent =
        Role.builder()
            .user(User.builder().id(cmd.userId()).build())
            .email(email)
            .role(cmd.role())
            .build();

    var vEnt = validator.validate(ent);
    if (!vEnt.isEmpty()) throw new ConstraintViolationException(vEnt);

    if (repo.existsByEmail(email)) throw new DuplicateEmailException(email);
    if (cmd.role() == UserRole.FORMER_STUDENT && repo.existsFormerStudentByUserId(cmd.userId()))
      throw new FormerStudentRegistrationException(User.builder().id(cmd.userId()).build());

    repo.persist(ent);
    repo.flush();
    return ent.getId();
  }
}
