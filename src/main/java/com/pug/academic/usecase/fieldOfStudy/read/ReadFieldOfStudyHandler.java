package com.pug.academic.usecase.fieldOfStudy.read;

import com.pug.academic.domain.FieldOfStudy;
import com.pug.academic.domain.exceptions.FieldOfStudyNotFoundException;
import com.pug.academic.infra.persistence.FieldOfStudyRepository;
import com.pug.shared.dtos.ReadByIdQuery;
import com.pug.shared.dtos.ReadByPatternQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.List;

@ApplicationScoped
public class ReadFieldOfStudyHandler {

  @Inject FieldOfStudyRepository repo;
  @Inject Validator validator;

  public FieldOfStudy handle(ReadByIdQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);
    return repo.findByIdOptional(q.id())
        .orElseThrow(() -> new FieldOfStudyNotFoundException(q.id()));
  }

  public List<FieldOfStudy> handle(ReadByPatternQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);
    if (q.limit() != null) return repo.listByPattern(q.query(), q.limit());
    return repo.listAllSorted();
  }
}
