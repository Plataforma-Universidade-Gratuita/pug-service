package com.pug.partner.infra.persistence;

import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.infra.StaffMapper;
import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class StaffRepositoryImpl
    implements StaffRepository, PanacheRepositoryBase<StaffEntity, UUID> {

  @Override
  public Optional<Staff> findOptionalById(UUID id) {
    return findByIdOptional(id).map(StaffMapper::toDomain);
  }

  @Override
  public Optional<Staff> findByUserAndEntity(UUID userId, UUID entityId) {
    return find("userId = ?1 and entityId = ?2", userId, entityId)
        .firstResultOptional()
        .map(StaffMapper::toDomain);
  }

  @Override
  public Optional<Staff> findByEmail(String email) {
    if (email == null) return Optional.empty();
    String el = email.trim().toLowerCase(Locale.ROOT);
    return find("lower(email) = ?1", el).firstResultOptional().map(StaffMapper::toDomain);
  }

  @Override
  public boolean existsByEmailForAnother(String email, UUID excludeId) {
    if (email == null) return false;
    String el = email.trim().toLowerCase(Locale.ROOT);
    long n = count("lower(email) = ?1 and id <> ?2", el, excludeId);
    return n > 0;
  }

  @Override
  public Staff save(Staff domain) {
    if (domain.getId() == null) {
      var e = StaffMapper.toEntity(domain);
      persist(e);
      flush();
      return StaffMapper.toDomain(e);
    }
    var managed = findById(domain.getId());
    if (managed == null) {
      var e = StaffMapper.toEntity(domain);
      persist(e);
      flush();
      return StaffMapper.toDomain(e);
    }
    StaffMapper.copy(domain, managed);
    flush();
    return StaffMapper.toDomain(managed);
  }

  @Override
  public Page<Staff> listByEntity(UUID entityId, PageRequest pr) {
    var q = find("entityId = ?1 order by email asc", entityId);
    var items = q.page(pr.page(), pr.size()).list().stream().map(StaffMapper::toDomain).toList();
    long total = q.count();
    return new Page<>(items, total, pr.page(), pr.size());
  }
}
