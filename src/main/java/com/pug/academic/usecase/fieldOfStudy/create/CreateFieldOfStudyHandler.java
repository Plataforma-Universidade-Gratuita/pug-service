package com.pug.academic.usecase.fieldOfStudy.create;

import com.pug.academic.domain.FieldOfStudy;
import com.pug.academic.domain.exceptions.DuplicateFieldOfStudyNameException;
import com.pug.academic.infra.persistence.FieldOfStudyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Locale;
import java.util.UUID;

@ApplicationScoped
public class CreateFieldOfStudyHandler {

  @Inject FieldOfStudyRepository repo;
  @Inject Validator validator;

  @Transactional
  public UUID handle(CreateFieldOfStudyCommand cmd) {
    var vCmd = validator.validate(cmd);
    if (!vCmd.isEmpty()) throw new ConstraintViolationException(vCmd);

    String name = cmd.name().trim().toLowerCase(Locale.ROOT);

    if (repo.existsByName(name)) throw new DuplicateFieldOfStudyNameException(name);

    var entity = FieldOfStudy.builder().name(name).build();
    var vEnt = validator.validate(entity);
    if (!vEnt.isEmpty()) throw new ConstraintViolationException(vEnt);

    repo.persist(entity);
    repo.flush();
    return entity.getId();
  }
}
