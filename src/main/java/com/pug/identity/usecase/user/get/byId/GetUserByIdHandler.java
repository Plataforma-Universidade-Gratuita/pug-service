package com.pug.identity.usecase.user.get.byId;

import com.pug.identity.domain.User;
import com.pug.identity.domain.exceptions.UserNotFoundException;
import com.pug.identity.infra.persistence.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class GetUserByIdHandler {
  @Inject UserRepository repo;

  @Transactional(Transactional.TxType.SUPPORTS)
  public User handle(GetUserByIdQuery q) {
    return repo.findByIdOptional(q.id()).orElseThrow(() -> new UserNotFoundException(q.id()));
  }
}
