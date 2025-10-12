package com.pug.geo.infra.persistence;

import com.pug.geo.domain.City;
import com.pug.geo.domain.CityRepository;
import com.pug.geo.infra.CityMapper;
import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CityRepositoryImpl implements CityRepository, PanacheRepositoryBase<CityEntity, UUID> {

  @Override
  public Page<City> listByPattern(String pattern, PageRequest pr) {
    String p = pattern == null ? "" : pattern.trim();
    if (p.isEmpty()) return new Page<>(List.of(), 0, pr.page(), pr.size());

    String fold = fold(p);
    List<CityEntity> all = list("order by name asc");
    List<CityEntity> matched =
        all.stream().filter(e -> fold(e.getName()).startsWith(fold)).toList();

    long total = matched.size();
    List<City> items =
        matched.stream().skip(pr.offset()).limit(pr.size()).map(CityMapper::toDomain).toList();

    return new Page<>(items, total, pr.page(), pr.size());
  }

  @Override
  public City save(City city) {
    CityEntity e = city.getId() == null ? CityMapper.toEntity(city) : findById(city.getId());
    if (e == null) {
      e = CityMapper.toEntity(city);
      persist(e);
    } else {
      CityMapper.copy(city, e);
    }
    return CityMapper.toDomain(e);
  }

  @Override
  public Optional<City> findOptionalById(UUID id) {
    return Optional.ofNullable(findById(id)).map(CityMapper::toDomain);
  }

  @Override
  public Optional<City> findByIbgeCode(String ibgeCodeDigits) {
    if (ibgeCodeDigits == null) return Optional.empty();
    return find("ibgeCode = ?1", ibgeCodeDigits).firstResultOptional().map(CityMapper::toDomain);
  }

  private static String fold(String s) {
    String n = Normalizer.normalize(s == null ? "" : s, Normalizer.Form.NFD);
    return n.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
  }
}
