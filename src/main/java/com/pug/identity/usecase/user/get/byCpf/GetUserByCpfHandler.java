package com.pug.identity.usecase.user.get.byCpf;

import com.pug.identity.domain.User;
import com.pug.identity.domain.exceptions.UserNotFoundException;
import com.pug.identity.infra.persistence.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class GetUserByCpfHandler {
  @Inject UserRepository repo;

  @Transactional(Transactional.TxType.SUPPORTS)
  public User handle(GetUserByCpfQuery q) {
    String cpf = q.cpf() == null ? null : q.cpf().replaceAll("\\D", "");
    return repo.findByCpf(cpf).orElseThrow(() -> new UserNotFoundException(cpf));
  }
}
