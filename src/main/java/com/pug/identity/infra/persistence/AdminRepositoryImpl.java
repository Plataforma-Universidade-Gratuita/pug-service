package com.pug.identity.infra.persistence;

import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminRepository;
import com.pug.identity.infra.AdminMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AdminRepositoryImpl
    implements PanacheRepositoryBase<AdminEntity, UUID>, AdminRepository {

  @Override
  public boolean isAdmin(UUID userId) {
    return findById(userId) != null;
  }

  @Override
  @Transactional
  public void grant(UUID userId) {
    if (findById(userId) == null) {
      var e = new AdminEntity(userId);
      persist(e);
      getEntityManager().flush();
      getEntityManager().refresh(e);
    }
  }

  @Override
  @Transactional
  public void revoke(UUID userId) {
    deleteById(userId);
  }

  @Override
  public Optional<Admin> findByUserId(UUID userId) {
    var e = findById(userId);
    return Optional.ofNullable(e).map(AdminMapper::toDomain);
  }

  @Override
  public List<Admin> listAllAdmins() {
    return findAll().stream().map(AdminMapper::toDomain).toList();
  }
}
