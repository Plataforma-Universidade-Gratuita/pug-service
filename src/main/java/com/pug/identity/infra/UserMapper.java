package com.pug.identity.infra;

import com.pug.identity.domain.Cpf;
import com.pug.identity.domain.User;
import com.pug.identity.infra.persistence.UserEntity;

public final class UserMapper {
  private UserMapper() {}

  public static User toDomain(UserEntity e) {
    if (e == null) return null;
    return User.builder().id(e.getId()).cpf(Cpf.of(e.getCpf())).name(e.getName()).build();
  }

  public static UserEntity toEntity(User d) {
    if (d == null) return null;
    return UserEntity.builder().id(d.getId()).cpf(d.getCpf().getValue()).name(d.getName()).build();
  }

  public static void copy(User d, UserEntity e) {
    e.setCpf(d.getCpf().getValue());
    e.setName(d.getName());
  }
}
