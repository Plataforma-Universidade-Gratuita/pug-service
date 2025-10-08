package com.pug.academic.infra.persistence;

import com.pug.academic.domain.Course;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@ApplicationScoped
public class CourseRepository implements PanacheRepositoryBase<Course, UUID> {
  public boolean existsByName(String name) {
    return count("name = ?1", name) > 0;
  }

  public boolean existsByNameForAnother(String name, UUID id) {
    return count("name = ?1 and id <> ?2", name, id) > 0;
  }

  public List<Course> listAllSorted() {
    return list("order by name asc");
  }

  public List<Course> listByPattern(String q, int limit, int offset) {
    String normalized =
        Normalizer.normalize(q, Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "")
            .toLowerCase(Locale.ROOT);
    String like = "%" + normalized + "%";

    return find(
            "concat('', function('immutable_unaccent', lower(name))) like ?1 order by name asc",
            like)
        .page(Page.ofSize(limit))
        .list();
  }
}
