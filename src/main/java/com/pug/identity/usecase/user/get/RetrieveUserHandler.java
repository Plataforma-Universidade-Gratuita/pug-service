package com.pug.identity.usecase.user.get;

import com.pug.identity.domain.User;
import com.pug.identity.domain.exceptions.UserNotFoundException;
import com.pug.identity.infra.persistence.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class RetrieveUserHandler {
  @Inject UserRepository repo;
  @Inject Validator validator;

  @Transactional(Transactional.TxType.SUPPORTS)
  public User handle(RetrieveUserByIdQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);
    return repo.findByIdOptional(q.id()).orElseThrow(() -> new UserNotFoundException(q.id()));
  }

  @Transactional(Transactional.TxType.SUPPORTS)
  public User handle(RetrieveUserByCpfQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);

    String cpf = q.cpf().replaceAll("\\D", "");
    return repo.findByCpf(cpf).orElseThrow(() -> new UserNotFoundException(cpf));
  }
}
