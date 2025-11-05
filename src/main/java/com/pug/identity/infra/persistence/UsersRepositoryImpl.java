package com.pug.identity.infra.persistence;

import com.pug.identity.domain.UsersRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class UsersRepositoryImpl
    implements UsersRepository, PanacheRepositoryBase<UsersEntity, UUID> {
  @Override
  public void persist(UsersEntity user) {
    persistAndFlush(user);
  }
}
