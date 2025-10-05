package com.pug.geo.infra.persistence;

import com.pug.geo.domain.City;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CityRepository implements PanacheRepositoryBase<City, UUID> {
  public List<City> listAllSorted() {
    return list("order by name asc");
  }

  public List<City> listByPattern(String q, int limit) {
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

  public Optional<City> findByIbgeCode(String code) {
    return find("ibgeCode = ?1", code).firstResultOptional();
  }
}
