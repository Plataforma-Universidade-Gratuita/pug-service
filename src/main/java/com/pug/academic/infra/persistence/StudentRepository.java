package com.pug.academic.infra.persistence;

import com.pug.academic.domain.Student;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class StudentRepository implements PanacheRepositoryBase<Student, UUID> {
  public boolean existsByAcademicRegistration(String reg) {
    return count("academicRegistration = ?1", reg) > 0;
  }

  public boolean existsByAcademicRegistrationForAnother(String reg, UUID id) {
    return count("academicRegistration = ?1 and id <> ?2", reg, id) > 0;
  }

  public Optional<Student> findByAcademicRegistration(String reg) {
    return find("academicRegistration", reg).firstResultOptional();
  }

  public Optional<Student> findByUserRoleId(UUID userRoleId) {
    return find("userRole.id", userRoleId).firstResultOptional();
  }
}
