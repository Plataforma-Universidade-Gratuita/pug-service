package com.pug.partner.domain;

import com.pug.partner.infra.persistence.EntitiesEntity;

public interface EntitiesRepository {
  void persist(EntitiesEntity entity);
}
