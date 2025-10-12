package com.pug.identity.usecase.user.create;

import com.pug.identity.domain.User;
import com.pug.identity.domain.exceptions.DuplicateCpfException;
import com.pug.identity.infra.persistence.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.UUID;

@ApplicationScoped
public class CreateUserHandler {
  @Inject UserRepository repo;
  @Inject Validator validator;

  @Transactional
  public UUID handle(CreateUserCommand cmd) {
    var vCmd = validator.validate(cmd);
    if (!vCmd.isEmpty()) throw new ConstraintViolationException(vCmd);

    String cpf = cmd.cpf().replaceAll("\\D", "");

    var probe = User.builder().cpf(cpf).name(cmd.name()).build();
    var vEnt = validator.validate(probe);
    if (!vEnt.isEmpty()) throw new ConstraintViolationException(vEnt);

    if (repo.existsByCpf(cpf)) throw new DuplicateCpfException(cpf);

    repo.persist(probe);
    repo.flush();
    return probe.getId();
  }
}
