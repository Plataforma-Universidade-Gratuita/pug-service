package br.org.catolicasc.pug.partner.infra.persistence;

import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.shared.infra.persistence.BaseAuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * JPA entity representing a Partner Organization (Entity) within the persistence layer.
 *
 * <p>This class acts as the database-mapped counterpart to the {@link
 * br.org.catolicasc.pug.partner.domain.Entity} domain aggregate. It inherits a time-ordered UUIDv7
 * primary key and standard audit tracking fields from {@link BaseAuditedEntity}. It enforces strict
 * uniqueness on the CNPJ at the database level.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
    callSuper = true,
    of = {"name", "cnpj"})
@Entity
@Table(
    name = "entities",
    indexes = {
      @Index(name = "idx_entities_name", columnList = "name"),
      @Index(name = "idx_entities_city", columnList = "city_id")
    })
@SuperBuilder
public class EntityEntity extends BaseAuditedEntity {

  /**
   * The unique 14-digit corporate identification number (CNPJ).
   *
   * <p>Mapped as a fixed-length {@code CHAR(14)} at the database level for optimal storage and
   * indexing. This serves as a natural key for the partner entity, enforced by a database unique
   * constraint.
   */
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "cnpj", nullable = false, length = 14, unique = true)
  private String cnpj;

  /**
   * The registered name or corporate reason of the partner entity.
   */
  @Column(name = "name", nullable = false, length = 150)
  private String name;

  /**
   * The unique identifier (UUID) of the associated {@link City}.
   *
   * <p>Acts as a foreign key linking this partner organization to its geographical location.
   */
  @Column(name = "city_id", nullable = false)
  private UUID cityId;

  /** The physical street address of the partner organization. */
  @Column(name = "address", length = 254)
  private String address;
}
