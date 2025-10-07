package com.pug.identity.usecase.user.update;

import com.pug.identity.domain.User;
import com.pug.identity.domain.exceptions.DuplicateCpfException;
import com.pug.identity.domain.exceptions.UserNotFoundException;
import com.pug.identity.infra.persistence.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.UUID;

@ApplicationScoped
public class UpdateUserHandler {
  @Inject UserRepository repo;
  @Inject Validator validator;

  @Transactional
  public User handle(UpdateUserCommand cmd) {
    var vCmd = validator.validate(cmd);
    if (!vCmd.isEmpty()) throw new ConstraintViolationException(vCmd);

    UUID id = cmd.id();
    String cpf = cmd.cpf().replaceAll("\\D", "");

    var u = repo.findByIdOptional(id).orElseThrow(() -> new UserNotFoundException(id));

    if (repo.existsByCpfForAnother(cpf, id)) throw new DuplicateCpfException(cpf);

    var probe = User.builder().id(u.getId()).cpf(cpf).name(cmd.name()).build();
    var v = validator.validate(probe);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);

    u.setCpf(cpf);
    u.setName(cmd.name());
    repo.flush();
    return u;
  }
}
