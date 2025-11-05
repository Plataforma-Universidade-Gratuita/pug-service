package com.pug.partner.domain;

import com.pug.partner.infra.persistence.StaffEntity;

public interface StaffRepository {
  void persist(StaffEntity entity);
}
