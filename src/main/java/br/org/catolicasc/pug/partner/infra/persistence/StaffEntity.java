package br.org.catolicasc.pug.partner.infra.persistence;

import br.org.catolicasc.pug.partner.domain.Staff;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * JPA entity representing Staff privileges within the persistence layer.
 *
 * <p>This class is the database-mapped counterpart to the {@link Staff}
 * domain aggregate. Instead of a standalone ID, it uses the linked account's UUID as its primary
 * key, effectively functioning as a one-to-one extension of an authentication account to grant
 * staff-level access tied to a specific Partner Entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "accountId")
@ToString(of = {"accountId", "entityId"})
@Entity
@Table(
    name = "staff",
    indexes = {@Index(name = "idx_staff_entity", columnList = "entity_id")})
@Builder(toBuilder = true)
public class StaffEntity {

  /**
   * The unique identifier of the linked authentication account.
   *
   * <p>Serves dual purpose as both the primary key for this entity and the logical foreign key to
   * the identity accounts table. It is strictly immutable once persisted.
   */
  @Id
  @Column(name = "account_id", nullable = false, updatable = false)
  private UUID accountId;

  /**
   * The unique identifier of the linked {@link EntityEntity}.
   *
   * <p>Acts as a foreign key representing the partner organization this staff member works for.
   */
  @Column(name = "entity_id", nullable = false)
  private UUID entityId;
}
