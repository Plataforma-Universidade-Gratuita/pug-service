package com.pug.academic.usecase.student.read;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.exceptions.StudentNotFoundException;
import com.pug.academic.infra.persistence.StudentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class ReadStudentHandler {

  @Inject StudentRepository repo;
  @Inject Validator validator;

  public Student handle(ReadStudentByAcademicRegistrationQuery cmd) {
    var v = validator.validate(cmd);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);
    String reg = cmd.academicRegistration().trim();
    return repo.findByAcademicRegistration(reg)
        .orElseThrow(() -> new StudentNotFoundException(reg));
  }

  public Student handle(ReadStudentByUserRoleIdQuery cmd) {
    var v = validator.validate(cmd);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);
    return repo.findByUserRoleId(cmd.userRoleId())
        .orElseThrow(() -> new StudentNotFoundException(cmd.userRoleId()));
  }
}
