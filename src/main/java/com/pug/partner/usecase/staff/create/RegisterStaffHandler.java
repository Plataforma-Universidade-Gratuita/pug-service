package com.pug.partner.usecase.staff.create;

import com.pug.identity.domain.Role;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.exceptions.DuplicateStaffException;
import com.pug.partner.infra.persistence.StaffRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class RegisterStaffHandler {

  @Inject StaffRepository repo;
  @Inject EntityManager em;
  @Inject Validator validator;

  @Transactional
  public Staff handle(RegisterStaffCommand cmd) {
    var vCmd = validator.validate(cmd);
    if (!vCmd.isEmpty()) throw new ConstraintViolationException(vCmd);

    if (repo.findByUserRoleId(cmd.userRoleId()).isPresent()) {
      throw new DuplicateStaffException(cmd.userRoleId());
    }

    var staff =
        Staff.builder()
            .userRole(em.getReference(Role.class, cmd.userRoleId()))
            .entity(em.getReference(PartnerEntity.class, cmd.entityId()))
            .build();

    var vAgg = validator.validate(staff);
    if (!vAgg.isEmpty()) throw new ConstraintViolationException(vAgg);

    repo.persist(staff);
    repo.flush();
    return staff;
  }
}
