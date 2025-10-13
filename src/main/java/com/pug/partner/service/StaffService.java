package com.pug.partner.service;

import static com.pug.partner.domain.PartnerErrorCodes.STAFF_EMAIL_ALREADY_EXISTS;
import static com.pug.partner.domain.PartnerErrorCodes.STAFF_NOT_FOUND;

import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.service.commands.CreateStaffCommand;
import com.pug.shared.application.EmailQuery;
import com.pug.shared.application.UuidCommand;
import com.pug.shared.domain.exceptions.AppValidationException;
import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class StaffService {

  @Inject StaffRepository repo;

  @Transactional
  public Staff add(CreateStaffCommand cmd) {
    if (repo.existsByEmailForAnother(cmd.email(), null)) {
      throw new AppValidationException(STAFF_EMAIL_ALREADY_EXISTS);
    }
    Staff s =
        Staff.newActive()
            .id(UUID.randomUUID())
            .userId(cmd.userId())
            .email(cmd.email())
            .entityId(cmd.entityId())
            .build();
    return repo.save(s);
  }

  @Transactional
  public Staff activate(UuidCommand cmd) {
    Staff s =
        repo.findOptionalById(cmd.id())
            .orElseThrow(() -> new AppValidationException(STAFF_NOT_FOUND));
    return repo.save(s.activate());
  }

  @Transactional
  public Staff deactivate(UuidCommand cmd) {
    Staff s =
        repo.findOptionalById(cmd.id())
            .orElseThrow(() -> new AppValidationException(STAFF_NOT_FOUND));
    return repo.save(s.deactivate());
  }

  public Optional<Staff> findByEmail(EmailQuery q) {
    return repo.findByEmail(q.email());
  }

  public Page<Staff> listByEntity(UUID entityId, PageRequest pr) {
    return repo.listByEntity(entityId, pr);
  }
}
