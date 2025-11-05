package com.pug.identity.domain;

import com.pug.identity.infra.persistence.AdminsEntity;

public interface AdminsRepository {
  void persist(AdminsEntity entity);
}
