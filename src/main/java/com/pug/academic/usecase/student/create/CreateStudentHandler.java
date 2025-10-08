package com.pug.academic.usecase.student.create;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.exceptions.CourseNotFoundException;
import com.pug.academic.domain.exceptions.DuplicateAcademicRegistrationException;
import com.pug.academic.infra.persistence.CourseRepository;
import com.pug.academic.infra.persistence.StudentRepository;
import com.pug.identity.domain.exceptions.RoleNotFoundException;
import com.pug.identity.infra.persistence.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.UUID;

@ApplicationScoped
public class CreateStudentHandler {

  @Inject StudentRepository repo;
  @Inject RoleRepository roleRepo;
  @Inject CourseRepository courseRepo;
  @Inject Validator validator;

  @Transactional
  public UUID handle(CreateStudentCommand cmd) {
    var vCmd = validator.validate(cmd);
    if (!vCmd.isEmpty()) throw new ConstraintViolationException(vCmd);

    String reg = cmd.academicRegistration().trim();

    if (repo.existsByAcademicRegistration(reg))
      throw new DuplicateAcademicRegistrationException(reg);

    var role =
        roleRepo
            .findByIdOptional(cmd.userRoleId())
            .orElseThrow(() -> new RoleNotFoundException(cmd.userRoleId()));

    var course =
        courseRepo
            .findByIdOptional(cmd.courseId())
            .orElseThrow(() -> new CourseNotFoundException(cmd.courseId()));

    var entity = Student.builder().userRole(role).academicRegistration(reg).course(course).build();

    var vEnt = validator.validate(entity);
    if (!vEnt.isEmpty()) throw new ConstraintViolationException(vEnt);

    repo.persist(entity);
    repo.flush();
    return entity.getId();
  }
}
