package com.pug.geo.domain;

import com.pug.geo.infra.persistence.CitiesEntity;

public interface CitiesRepository {
  void persist(CitiesEntity entity);
}
