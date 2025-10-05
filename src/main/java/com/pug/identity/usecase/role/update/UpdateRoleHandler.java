package com.pug.identity.usecase.role.update;

import com.pug.identity.domain.Role;
import com.pug.identity.domain.enums.UserRole;
import com.pug.identity.domain.exceptions.DuplicateEmailException;
import com.pug.identity.domain.exceptions.FormerStudentRegistrationException;
import com.pug.identity.domain.exceptions.RoleNotFoundException;
import com.pug.identity.infra.persistence.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class UpdateRoleHandler {

  @Inject RoleRepository repo;
  @Inject Validator validator;

  @Transactional
  public Role handle(UpdateRoleCommand cmd) {
    var vCmd = validator.validate(cmd);
    if (!vCmd.isEmpty()) throw new ConstraintViolationException(vCmd);

    String email = cmd.email().trim().toLowerCase(java.util.Locale.ROOT);
    var entity =
        repo.findByIdOptional(cmd.id()).orElseThrow(() -> new RoleNotFoundException(cmd.id()));

    if (repo.existsByEmailForAnother(email, cmd.id())) {
      throw new DuplicateEmailException(email);
    }
    if (cmd.role() == UserRole.FORMER_STUDENT
        && repo.existsFormerStudentForAnother(entity.getUser().getId(), cmd.id())) {
      throw new FormerStudentRegistrationException(entity.getUser());
    }

    var probe =
        Role.builder()
            .id(entity.getId())
            .user(entity.getUser())
            .email(email)
            .role(cmd.role())
            .active(entity.isActive())
            .build();

    var vEntity = validator.validate(probe);
    if (!vEntity.isEmpty()) throw new ConstraintViolationException(vEntity);

    entity.setEmail(email);
    entity.setRole(cmd.role());
    repo.flush();
    return entity;
  }
}
