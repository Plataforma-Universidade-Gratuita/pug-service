package com.pug.identity.domain;

import com.pug.identity.infra.persistence.UsersEntity;

public interface UsersRepository {
  void persist(UsersEntity entity);
}
