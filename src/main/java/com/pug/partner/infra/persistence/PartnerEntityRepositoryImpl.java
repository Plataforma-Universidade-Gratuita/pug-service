package com.pug.partner.infra.persistence;

import com.pug.partner.domain.Cnpj;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.PartnerEntityRepository;
import com.pug.partner.infra.PartnerEntityMapper;
import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import com.pug.shared.infra.util.TextSearchUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PartnerEntityRepositoryImpl
    implements PartnerEntityRepository, PanacheRepositoryBase<PartnerEntityEntity, UUID> {

  @Override
  public Optional<PartnerEntity> findOptionalById(UUID id) {
    return findByIdOptional(id).map(PartnerEntityMapper::toDomain);
  }

  @Override
  public Optional<PartnerEntity> findByCnpj(String cnpjDigits) {
    if (cnpjDigits == null) return Optional.empty();
    String d = cnpjDigits.replaceAll("\\D", "");
    return find("cnpj", Cnpj.of(d)).firstResultOptional().map(PartnerEntityMapper::toDomain);
  }

  @Override
  public boolean existsByCnpjForAnother(String cnpjDigits, UUID excludeId) {
    if (cnpjDigits == null) return false;
    String d = cnpjDigits.replaceAll("\\D", "");
    long n = count("cnpj = ?1 and id <> ?2", Cnpj.of(d), excludeId);
    return n > 0;
  }

  @Override
  public PartnerEntity save(PartnerEntity domain) {
    if (domain.getId() == null) {
      var e = PartnerEntityMapper.toEntity(domain);
      persist(e);
      flush();
      return PartnerEntityMapper.toDomain(e);
    }
    var managed = findById(domain.getId());
    if (managed == null) {
      var e = PartnerEntityMapper.toEntity(domain);
      persist(e);
      flush();
      return PartnerEntityMapper.toDomain(e);
    }
    PartnerEntityMapper.copy(domain, managed);
    flush();
    return PartnerEntityMapper.toDomain(managed);
  }

  @Override
  public Page<PartnerEntity> listByCity(UUID cityId, PageRequest pr) {
    var q = find("cityId = ?1 order by name asc", cityId);
    var items =
        q.page(pr.page(), pr.size()).list().stream().map(PartnerEntityMapper::toDomain).toList();
    long total = q.count();
    return new Page<>(items, total, pr.page(), pr.size());
  }

  @Override
  public Page<PartnerEntity> searchByName(String pattern, PageRequest pr) {
    String p = pattern == null ? "" : pattern.trim();
    if (p.isEmpty()) return new Page<>(List.of(), 0, pr.page(), pr.size());

    String q = TextSearchUtils.fold(p);
    List<PartnerEntityEntity> all = list("order by name asc");
    List<PartnerEntityEntity> matched =
        all.stream().filter(e -> TextSearchUtils.fold(e.getName()).contains(q)).toList();

    long total = matched.size();
    List<PartnerEntity> items =
        matched.stream()
            .skip(pr.offset())
            .limit(pr.size())
            .map(PartnerEntityMapper::toDomain)
            .toList();

    return new Page<>(items, total, pr.page(), pr.size());
  }
}
