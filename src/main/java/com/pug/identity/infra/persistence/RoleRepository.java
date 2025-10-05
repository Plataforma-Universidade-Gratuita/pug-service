package com.pug.identity.infra.persistence;

import com.pug.identity.domain.Role;
import com.pug.identity.domain.enums.UserRole;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RoleRepository implements PanacheRepositoryBase<Role, UUID> {
  public boolean existsByEmail(String email) {
    return count("email = ?1", email) > 0;
  }

  public Optional<Role> findByEmail(String email) {
    return find("email", email).firstResultOptional();
  }

  public boolean existsFormerStudentByUserId(UUID userId) {
    return count("user.id = ?1 and role = ?2", userId, UserRole.FORMER_STUDENT) > 0;
  }

  public boolean existsByEmailForAnother(String email, UUID excludeId) {
    return count("email = ?1 and id <> ?2", email, excludeId) > 0;
  }

  public boolean existsFormerStudentForAnother(UUID userId, UUID excludeId) {
    return count(
            "user.id = ?1 and role = ?2 and id <> ?3", userId, UserRole.FORMER_STUDENT, excludeId)
        > 0;
  }
}
