package br.org.catolicasc.pug.academic.infra;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.infra.persistence.AreaOfExpertiseEntity;
import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.shared.domain.vos.AuditInfo;

/**
 * Stateless utility class responsible for mapping between AreaOfExpertise boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer, ensuring that the pure Domain model ({@link
 * AreaOfExpertise}) does not leak into or depend upon the JPA Persistence model ({@link
 * AreaOfExpertiseEntity}).
 */
public final class AreaOfExpertiseMapper {

  private AreaOfExpertiseMapper() {}

  /**
   * Reconstitutes a pure Domain {@link AreaOfExpertise} aggregate from a JPA {@link
   * AreaOfExpertiseEntity}.
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link AreaOfExpertise}, or {@code null} if the input entity
   *     is null
   */
  public static AreaOfExpertise toDomain(AreaOfExpertiseEntity e) {
    if (e == null) {
      return null;
    }
    return AreaOfExpertise.builder()
        .id(e.getId())
        .name(e.getName())
        .auditInfo(AuditInfo.factory(e.getCreatedAt(), e.getUpdatedAt()))
        .build();
  }

  /**
   * Translates a pure Domain {@link AreaOfExpertise} aggregate into a newly instantiated JPA {@link
   * AreaOfExpertiseEntity}.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link AreaOfExpertiseEntity}, or {@code null} if the input
   *     domain is null
   */
  public static AreaOfExpertiseEntity toEntity(AreaOfExpertise d) {
    if (d == null) {
      return null;
    }
    return AreaOfExpertiseEntity.builder()
        .id(d.getId())
        .name(d.getName())
        .createdAt(d.getAuditInfo().getCreatedAt())
        .updatedAt(d.getAuditInfo().getUpdatedAt())
        .build();
  }

  /**
   * Updates an existing, attached JPA {@link AreaOfExpertiseEntity} with the current state of a
   * Domain {@link AreaOfExpertise}.
   *
   * @param d the Domain aggregate containing the updated state
   * @param e the existing, attached JPA entity to update in-place
   */
  public static void copy(AreaOfExpertise d, AreaOfExpertiseEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setName(d.getName());
    e.setCreatedAt(d.getAuditInfo().getCreatedAt());
    e.setUpdatedAt(d.getAuditInfo().getUpdatedAt());
  }

  /**
   * Projects a JPA {@link AreaOfExpertiseEntity} into a lightweight, read-only {@link
   * AreaOfExpertiseView} DTO.
   *
   * @param s the JPA persistence entity to project
   * @return a flattened {@link AreaOfExpertiseView} DTO, or {@code null} if the input entity is
   *     null
   */
  public static AreaOfExpertiseView toView(AreaOfExpertiseEntity s) {
    if (s == null) {
      return null;
    }
    return new AreaOfExpertiseView(s.getId(), s.getName(), s.getCreatedAt(), s.getUpdatedAt());
  }
}
