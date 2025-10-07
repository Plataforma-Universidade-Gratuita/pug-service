package com.pug.academic.usecase.fieldOfStudy.update;

import com.pug.academic.domain.FieldOfStudy;
import com.pug.academic.domain.exceptions.DuplicateFieldOfStudyNameException;
import com.pug.academic.domain.exceptions.FieldOfStudyNotFoundException;
import com.pug.academic.infra.persistence.FieldOfStudyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Locale;
import java.util.UUID;

@ApplicationScoped
public class UpdateFieldOfStudyHandler {

  @Inject FieldOfStudyRepository repo;
  @Inject Validator validator;

  @Transactional
  public UUID handle(UpdateFieldOfStudyCommand cmd) {
    var v = validator.validate(cmd);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);

    UUID id = cmd.id();
    String norm = cmd.name().trim().toLowerCase(Locale.ROOT);

    FieldOfStudy entity =
        repo.findByIdOptional(id).orElseThrow(() -> new FieldOfStudyNotFoundException(id));

    if (repo.existsByNameForAnother(norm, id)) throw new DuplicateFieldOfStudyNameException(norm);

    entity.setName(norm);
    repo.flush();
    return entity.getId();
  }
}
