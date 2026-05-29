package br.org.catolicasc.pug.academic.infra.persistence;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.shared.infra.persistence.BaseAuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * JPA entity representing an Academic School within the persistence layer.
 *
 * <p>This class acts as the database-mapped counterpart to the {@link School} domain aggregate. It
 * inherits a time-ordered UUIDv7 primary key and standard audit tracking fields from {@link
 * BaseAuditedEntity}.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
    callSuper = true,
    of = {"name"})
@Entity
@Table(
    name = "schools",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_schools_name",
          columnNames = {"name"})
    })
public class SchoolEntity extends BaseAuditedEntity {

  /** The name of the academic school. */
  @Column(name = "name", nullable = false, length = 100)
  private String name;
}
