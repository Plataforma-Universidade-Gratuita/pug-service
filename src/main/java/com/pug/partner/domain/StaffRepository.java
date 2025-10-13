package com.pug.partner.domain;

import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import java.util.Optional;
import java.util.UUID;

public interface StaffRepository {
  Optional<Staff> findOptionalById(UUID id);

  Optional<Staff> findByUserAndEntity(UUID userId, UUID entityId);

  Optional<Staff> findByEmail(String email);

  boolean existsByEmailForAnother(String email, UUID excludeId);

  Staff save(Staff staff);

  Page<Staff> listByEntity(UUID entityId, PageRequest pr);
}
