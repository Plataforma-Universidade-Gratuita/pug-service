package com.pug.academic.domain;

import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import java.util.Optional;
import java.util.UUID;

public interface SchoolRepository {
  School save(School school);

  Optional<School> findOptionalById(UUID id);

  Optional<School> findByNameIgnoreCase(String name);

  Page<School> listOrdered(PageRequest pr);
}
