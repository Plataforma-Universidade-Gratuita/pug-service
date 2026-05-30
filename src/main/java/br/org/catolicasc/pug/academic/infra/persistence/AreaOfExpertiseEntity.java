package br.org.catolicasc.pug.academic.infra.persistence;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
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
 * JPA entity representing an Academic AreaOfExpertise within the persistence layer.
 *
 * <p>This class acts as the database-mapped counterpart to the {@link AreaOfExpertise} domain
 * aggregate. It inherits a time-ordered UUIDv7 primary key and standard audit tracking fields from
 * {@link BaseAuditedEntity}.
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
    name = "areas_of_expertise",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_areas_of_expertise_name",
          columnNames = {"name"})
    })
public class AreaOfExpertiseEntity extends BaseAuditedEntity {

  /** The name of the academic areaOfExpertise. */
  @Column(name = "name", nullable = false, length = 100)
  private String name;
}
