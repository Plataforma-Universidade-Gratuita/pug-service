package com.pug.identity.infra.persistence;

import com.pug.shared.domain.enums.Campi;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * JPA entity representing Administrator privileges within the persistence layer.
 * <p>
 * This class is the database-mapped counterpart to the {@link com.pug.identity.domain.Admin}
 * domain aggregate. Instead of a standalone ID, it uses the linked account's UUID
 * as its primary key, effectively functioning as a one-to-one extension of an
 * {@link AccountEntity} to grant system administration rights.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "accountId")
@ToString(of = {"accountId", "grantedAt"})
@Entity
@Table(name = "admins")
@Builder(toBuilder = true)
public class AdminEntity {

  /**
   * The unique identifier of the linked {@link AccountEntity}.
   * <p>
   * Serves dual purpose as both the primary key for this entity and the logical
   * foreign key to the accounts table. It is strictly immutable once persisted.
   */
  @Id
  @Column(name = "account_id", nullable = false, updatable = false)
  private UUID accountId;

  /**
   * The exact timestamp indicating when the administrative privileges were granted.
   * <p>
   * Used for auditing and security tracking. This field is immutable.
   */
  @Column(name = "granted_at", nullable = false, updatable = false)
  private OffsetDateTime grantedAt;

  /**
   * The designated university campus where this administrator has operational jurisdiction.
   * <p>
   * Stored as a string representation of the {@link Campi} enum.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "campus", nullable = false, length = 16)
  private Campi campus;
}