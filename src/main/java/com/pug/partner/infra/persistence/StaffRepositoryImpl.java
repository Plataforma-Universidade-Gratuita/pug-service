package com.pug.partner.infra.persistence;

import com.pug.partner.domain.StaffRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class StaffRepositoryImpl
    implements StaffRepository, PanacheRepositoryBase<StaffEntity, UUID> {
  @Override
  public void persist(StaffEntity staff) {
    persistAndFlush(staff);
  }
}
