package com.pug.identity.infra.persistence;

import com.pug.identity.domain.AdminsRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class AdminsRepositoryImpl
    implements AdminsRepository, PanacheRepositoryBase<AdminsEntity, UUID> {
  @Override
  public void persist(AdminsEntity admin) {
    persistAndFlush(admin);
  }
}
