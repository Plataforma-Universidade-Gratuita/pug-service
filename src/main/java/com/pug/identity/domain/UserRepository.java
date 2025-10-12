package com.pug.identity.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  Optional<User> findOptionalById(UUID id);

  Optional<User> findByCpf(String cpf);

  boolean existsByCpf(String cpf);

  boolean existsByCpfForAnother(String cpf, UUID notId);

  User save(User user);
}
