package com.pug.academic.service;

import com.pug.academic.domain.School;
import com.pug.academic.domain.SchoolRepository;
import com.pug.shared.application.StringQuery;
import com.pug.shared.application.UuidQuery;
import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.util.Optional;

@ApplicationScoped
public class SchoolService {
  @Inject SchoolRepository repo;

  public Optional<School> getById(@Valid UuidQuery q) {
    return repo.findOptionalById(q.id());
  }

  public Optional<School> getByName(@Valid StringQuery q) {
    return repo.findByNameIgnoreCase(q.value());
  }

  public Page<School> listOrdered(PageRequest pr) {
    return repo.listOrdered(pr);
  }
}
