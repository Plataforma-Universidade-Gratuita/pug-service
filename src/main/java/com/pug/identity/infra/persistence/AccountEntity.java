package com.pug.identity.infra.persistence;

import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.infra.persistence.BaseAuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * JPA entity representing an authentication Account within the persistence layer.
 *
 * <p>This class is the database-mapped counterpart to the {@link com.pug.identity.domain.Account}
 * domain aggregate. It inherits a time-ordered UUIDv7 primary key and standard audit fields from
 * {@link BaseAuditedEntity}. It maintains the credentials, role types, and the logical association
 * to a specific {@link UserEntity}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
    callSuper = true,
    of = {"email", "userId"})
@Entity
@Table(
    name = "accounts",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_accounts_email",
          columnNames = {"email"})
    },
    indexes = {
      @Index(name = "idx_accounts_email", columnList = "email"),
    })
@SuperBuilder
public class AccountEntity extends BaseAuditedEntity {

  /**
   * The unique identifier (UUID) of the associated {@link UserEntity}.
   *
   * <p>Acts as a foreign key linking this authentication account to a specific person. Once
   * created, this association is immutable.
   */
  @Column(name = "user_id", nullable = false, updatable = false)
  private UUID userId;

  /**
   * The unique email address used for authentication.
   *
   * <p>This field serves as a natural key for logging in and is protected by the {@code
   * uq_accounts_email} unique database constraint.
   */
  @Column(name = "email", nullable = false, length = 254)
  private String email;

  /**
   * The designated authorization role for this account (e.g., ADMIN, STUDENT).
   *
   * <p>Stored as a string representation of the {@link AccountType} enum.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "account_type", nullable = false, length = 16)
  private AccountType accountType;

  /** The securely hashed representation of the user's password. */
  @Column(name = "password_hash", nullable = false)
  private String passwordHash;
}
