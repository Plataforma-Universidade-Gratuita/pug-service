package com.pug.identity.usecase.user.get.byCpf;

import com.pug.identity.domain.User;
import com.pug.identity.domain.exceptions.UserNotFoundException;
import com.pug.identity.infra.persistence.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class GetUserByCpfHandler {
  @Inject UserRepository repo;
  @Inject Validator validator;

  @Transactional(Transactional.TxType.SUPPORTS)
  public User handle(GetUserByCpfQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);

    String cpf = q.cpf().replaceAll("\\D", "");
    return repo.findByCpf(cpf).orElseThrow(() -> new UserNotFoundException(cpf));
  }
}
