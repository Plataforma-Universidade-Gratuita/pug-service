package com.pug.identity.usecase.user.create;

import com.pug.identity.domain.User;
import com.pug.identity.domain.exceptions.DuplicateCpfException;
import com.pug.identity.infra.persistence.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import java.util.UUID;

@ApplicationScoped
public class CreateUserHandler {
  @Inject UserRepository repo;
  @Inject Validator validator;

  @Transactional
  public UUID handle(CreateUserCommand cmd) {
    String cpf = cmd.cpf() == null ? null : cmd.cpf().replaceAll("\\D", "");
    User u = User.builder().cpf(cpf).name(cmd.name()).build();

    var v = validator.validate(u);
    if (!v.isEmpty()) throw new jakarta.validation.ConstraintViolationException(v);

    if (repo.existsByCpf(cpf)) throw new DuplicateCpfException(cpf);

    repo.persist(u);
    repo.flush();
    return u.getId();
  }
}
