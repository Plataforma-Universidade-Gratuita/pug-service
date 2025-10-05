package com.pug.identity.infra.persistence;

import com.pug.identity.domain.User;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, UUID> {
  public boolean existsByCpf(String cpf) {
    return count("cpf = ?1", cpf) > 0;
  }

  public boolean existsByCpfForAnother(String cpf, UUID excludeId) {
    return count("cpf = ?1 and id <> ?2", cpf, excludeId) > 0;
  }

  public Optional<User> findByCpf(String cpf) {
    return find("cpf = ?1", cpf).firstResultOptional();
  }
}
