package com.pug.identity.infra.persistence;

import com.pug.identity.domain.Cpf;
import com.pug.identity.domain.User;
import com.pug.identity.domain.UserRepository;
import com.pug.identity.infra.UserMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository, PanacheRepositoryBase<UserEntity, UUID> {

  @Override
  public Optional<User> findOptionalById(UUID id) {
    return Optional.ofNullable(findById(id)).map(UserMapper::toDomain);
  }

  @Override
  public Optional<User> findByCpf(String cpf) {
    String d = Cpf.digits(cpf);
    return find("cpf", d).firstResultOptional().map(UserMapper::toDomain);
  }

  @Override
  public boolean existsByCpf(String cpf) {
    String d = Cpf.digits(cpf);
    return count("cpf = ?1", d) > 0;
  }

  @Override
  public boolean existsByCpfForAnother(String cpf, UUID notId) {
    String d = Cpf.digits(cpf);
    return count("cpf = ?1 and id <> ?2", d, notId) > 0;
  }

  @Override
  public User save(User user) {
    var e = UserMapper.toEntity(user);
    if (e.getId() == null) {
      PanacheRepositoryBase.super.persist(e);
      return UserMapper.toDomain(e);
    }
    UserMapper.copy(user, e);
    return UserMapper.toDomain(e);
  }
}
