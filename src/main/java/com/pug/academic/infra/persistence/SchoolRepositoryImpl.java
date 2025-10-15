package com.pug.academic.infra.persistence;

import com.pug.academic.domain.School;
import com.pug.academic.domain.SchoolRepository;
import com.pug.academic.infra.SchoolMapper;
import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SchoolRepositoryImpl
    implements SchoolRepository, PanacheRepositoryBase<SchoolEntity, UUID> {

  @Override
  public School save(School school) {
    if (school.getId() == null) {
      var e = SchoolMapper.toEntity(school);
      persist(e);
      return SchoolMapper.toDomain(e);
    }
    var e = findById(school.getId());
    SchoolMapper.copy(school, e);
    return SchoolMapper.toDomain(e);
  }

  @Override
  public Optional<School> findOptionalById(UUID id) {
    return Optional.ofNullable(findById(id)).map(SchoolMapper::toDomain);
  }

  @Override
  public Optional<School> findByNameIgnoreCase(String name) {
    if (name == null) return Optional.empty();
    return find("lower(name) = ?1", name.toLowerCase(Locale.ROOT))
        .firstResultOptional()
        .map(SchoolMapper::toDomain);
  }

  @Override
  public Page<School> listOrdered(PageRequest pr) {
    long total = count();
    List<School> items =
        find("order by name asc").page(pr.page(), pr.size()).list().stream()
            .map(SchoolMapper::toDomain)
            .toList();
    return new Page<>(items, total, pr.page(), pr.size());
  }
}
