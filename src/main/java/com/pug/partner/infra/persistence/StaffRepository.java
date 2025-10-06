package com.pug.partner.infra.persistence;

import com.pug.partner.domain.Staff;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class StaffRepository implements PanacheRepositoryBase<Staff, UUID> {
  public Optional<Staff> findByUserRoleId(UUID userRoleId) {
    return find("userRole.id", userRoleId).firstResultOptional();
  }
}
